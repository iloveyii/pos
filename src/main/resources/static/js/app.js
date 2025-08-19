// Product Data with Unsplash images
var products = [
    {
        id: 1,
        name: "Wireless Headphones",
        description: "Noise cancelling Bluetooth",
        price: 99.99,
        image: "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        category: "Electronics",
        stock: 15
    },
    {
        id: 2,
        name: "Smart Watch",
        description: "Fitness tracker & notifications",
        price: 149.99,
        image: "https://images.unsplash.com/photo-1523275335684-37898b6baf30?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        category: "Electronics",
        stock: 8
    },
    {
        id: 3,
        name: "Bluetooth Speaker",
        description: "Portable waterproof speaker",
        price: 59.99,
        image: "https://images.unsplash.com/photo-1572569511254-d8f925fe2cbb?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        category: "Electronics",
        stock: 12
    },
    {
        id: 4,
        name: "USB-C Cable",
        description: "Fast charging 3ft cable",
        price: 12.99,
        image: "https://images.unsplash.com/photo-1585771724684-38269d6639fd?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        category: "Accessories",
        stock: 42
    },
    {
        id: 5,
        name: "Wireless Mouse",
        description: "Ergonomic design",
        price: 24.99,
        image: "https://images.unsplash.com/photo-1527814050087-3793815479db?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        category: "Accessories",
        stock: 23
    },
    {
        id: 6,
        name: "Laptop Backpack",
        description: "Water resistant with USB port",
        price: 39.99,
        image: "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        category: "Accessories",
        stock: 7
    },
    {
        id: 7,
        name: "Power Bank",
        description: "10000mAh dual USB",
        price: 29.99,
        image: "https://images.unsplash.com/photo-1574944985070-8f3ebc6b79d2?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        category: "Electronics",
        stock: 18
    },
    {
        id: 8,
        name: "Screen Protector",
        description: "Tempered glass for smartphones",
        price: 8.99,
        image: "https://images.unsplash.com/photo-1546054454-aa26e2b734c7?ixlib=rb-4.0.3&auto=format&fit=crop&w=800&q=80",
        category: "Accessories",
        stock: 36
    }
];

// Cart Data
let cart = [];
let discount = {
    amount: 0,
    type: 'fixed' // 'fixed' or 'percent'
};
const taxRate = 0.08; // 8%
let objOrder = {
    id: null
};

// DOM Elements
const productsContainer = document.querySelector('.row-cols-2');
const cartItemsContainer = document.getElementById('cartItemsContainer');
const subtotalElement = document.getElementById('subtotal');
const taxElement = document.getElementById('tax');
const discountElement = document.getElementById('discount');
const totalElement = document.getElementById('total');
const clearCartBtn = document.getElementById('clearCart');
const applyDiscountBtn = document.getElementById('applyDiscount');
const discountAmountInput = document.getElementById('discountAmount');
const discountTypeSelect = document.getElementById('discountType');
const processPaymentBtn = document.getElementById('processPayment');
const notificationToast = new bootstrap.Toast(document.getElementById('notificationToast'));
const notificationMessage = document.getElementById('notificationMessage');

const pageContainers = document.querySelectorAll('.page-container');
const navLinks = document.querySelectorAll('.nav-link');
const searchProductsDashboard = document.querySelector('#searchProductsDashboard');
const changeCustomerBtn = document.querySelector('#changeCustomerBtn');
const saveCustomerBtn = document.getElementById('saveCustomerBtn');

// Initialize the POS
function initPOS() {
    console.log('initPOS');
    loadProducts();
    renderCart();
    setupEventListeners();
}
// Load Products
function loadProducts() {
    // Make GET request to /api/products
    fetch('/api/products')
      .then(response => {
        // Check if response is successful (status code 200-299)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json(); // Parse JSON response
      })
      .then(pros => {
        // Success - log products to console
        console.log('Products:', pros);
        products = pros;
        renderProducts(products);

        // You can also display them on the page
        // displayProducts(products);
      })
      .catch(error => {
        // Error handling
        console.error('Error fetching products:', error);

        // Show user-friendly message
        const shouldRefresh = confirm('Failed to load products. Click OK to refresh the page.');

        if (shouldRefresh) {
          window.location.reload(); // Refresh the page
        }
      });
}

// Render Products
function renderProducts(products) {
    console.log('inside renderProducts');
    productsContainer.innerHTML = '';

    products.forEach(product => {
        const productCard = document.createElement('div');
        productCard.className = 'col';
        productCard.innerHTML = `
            <div class="card product-card h-100" data-id="${product.id}">
                <img src="${product.image}" class="card-img-top product-img" alt="${product.name}">
                <div class="card-body">
                    <h5 class="card-title">${product.name}</h5>
                    <p class="card-text text-muted small">${product.description}</p>
                    <div class="d-flex justify-content-between align-items-center">
                        <span class="product-price">$${product.price.toFixed(2)}</span>
                        <span class="badge bg-success badge-stock">${product.inStock} in stock</span>
                    </div>
                </div>
            </div>
        `;
        productsContainer.appendChild(productCard);
    });
}

// Render Cart
function renderCart() {
    if (cart.length === 0) {
        cartItemsContainer.innerHTML = `
            <div class="empty-cart">
                <i class="fas fa-shopping-cart"></i>
                <p class="mb-0">Your cart is empty<br><small class="text-muted">Add products to get started</small></p>
            </div>
        `;
        return;
    }

    cartItemsContainer.innerHTML = '';

    cart.forEach(item => {
        const product = products.find(p => p.id === item.productId);
        const cartItem = document.createElement('div');
        cartItem.className = 'cart-item';
        cartItem.innerHTML = `
            <div class="d-flex justify-content-between">
                <div class="cart-item-name">
                    <h6 class="mb-1">${product.name}</h6>
                    <small class="text-muted">$${product.price.toFixed(2)} × ${item.quantity}</small>
                </div>
                <div class="text-end">
                    <span class="cart-item-price">$${(product.price * item.quantity).toFixed(2)}</span>
                    <div class="btn-group btn-group-sm ms-2">
                        <button class="btn quantity-btn decrease-quantity" data-id="${product.id}">-</button>
                        <input type="text" class="quantity-input" value="${item.quantity}" readonly>
                        <button class="btn quantity-btn increase-quantity" data-id="${product.id}">+</button>
                    </div>
                    <button class="btn btn-sm btn-outline-danger ms-1 remove-item" data-id="${product.id}">
                        <i class="fas fa-times"></i>
                    </button>
                </div>
            </div>
        `;
        cartItemsContainer.appendChild(cartItem);
    });

    updateTotals();
}

// Update Totals
function updateTotals() {
    const subtotal = cart.reduce((sum, item) => {
        const product = products.find(p => p.id === item.productId);
        return sum + (product.price * item.quantity);
    }, 0);

    let discountAmount = 0;
    if (discount.type === 'fixed') {
        discountAmount = discount.amount;
    } else if (discount.type === 'percent') {
        discountAmount = subtotal * (discount.amount / 100);
    }

    const tax = (subtotal - discountAmount) * taxRate;
    const total = subtotal - discountAmount + tax;

    subtotalElement.textContent = `$${subtotal.toFixed(2)}`;
    taxElement.textContent = `$${tax.toFixed(2)}`;
    discountElement.textContent = `-$${discountAmount.toFixed(2)}`;
    totalElement.textContent = `$${total.toFixed(2)}`;
}

// Add to Cart
function addToCart(productId, quantity = 1) {
    const existingItem = cart.find(item => item.productId === productId);

    if (existingItem) {
        existingItem.quantity += quantity;
        quantity = existingItem.quantity;
    } else {
        cart.push({
            productId,
            quantity
        });
    }

    renderCart();
    // updateCartOnBackend();
    addItemToOrderOnBackend(productId, quantity);
    showNotification(`${quantity} ${quantity > 1 ? 'items' : 'item'} added to cart`);
}

// Remove from Cart
function removeFromCart(productId) {
    cart = cart.filter(item => item.productId !== productId);
    renderCart();
    let orderId = 0;
    if(objOrder && objOrder.id){
        orderId = objOrder.id;
    }
    makeApiRequest('DELETE', `orders/${orderId}/items/${productId}`);
    showNotification('Item removed from cart');
}

// Update Quantity
function updateQuantity(productId, newQuantity) {
    const item = cart.find(item => item.productId === productId);

    if (item) {
        if (newQuantity <= 0) {
            removeFromCart(productId);
        } else {
            item.quantity = newQuantity;
            addItemToOrderOnBackend(productId, item.quantity);
        }
    }

    renderCart();
    // updateCartOnBackend();
}

// Apply Discount
function applyDiscount() {
    const amount = parseFloat(discountAmountInput.value) || 0;
    const type = discountTypeSelect.value;

    discount = {
        amount,
        type
    };

    updateTotals();
    showNotification('Discount applied');
}

// Clear Cart
function clearCart() {
    cart = [];
    discount = {
        amount: 0,
        type: 'fixed'
    };
    discountAmountInput.value = '';
    renderCart();
    showNotification('Cart cleared');
}

// Process Payment
function processPayment() {
    if (cart.length === 0) {
        showNotification('Cart is empty', 'danger');
        return;
    }
     clearCart();
     objOrder.id = null;
}

function updateCartOnBackend() {
    if(objOrder && objOrder.id)
        updateOrder(objOrder.id);
    else
        createOrder();
}

function addItemToOrderOnBackend(itemId, quantity) {
    console.log('addItemToOrderOnBackend', {itemId, quantity});
    // if order has been created
    if(objOrder && objOrder.id) {
        makeApiRequest('POST', `orders/${objOrder.id}/items`, {
            productId: itemId,
            quantity: quantity
        });
    } else { // order is new
        createOrder().then(() => {
            makeApiRequest('POST', `orders/${objOrder.id}/items`, {
                productId: itemId,
                quantity: quantity
            });
        });
    }
}

function createOrder() {
    // Create the order request
    return fetch('/api/orders', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            // Add authorization header if needed
            // 'Authorization': 'Bearer your-token-here'
        },
        body: JSON.stringify({
            items: cart  // Assuming your backend expects an "items" property
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(order => {
        console.log('Order created successfully:', order);
        showNotification('Order created successfully', 'success');
        objOrder.id = order.id;
        // clearCart();
        // You can redirect or update UI here
        // window.location.href = `/order-confirmation/${order.id}`;
    })
    .catch(error => {
        console.error('Error creating order:', error);
        alert('Failed to create order. Please try again.');
    });
}

function updateOrder(id) {
    // Create the order request
    fetch(`/api/orders/${id}`, {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json',
            // Add authorization header if needed
            // 'Authorization': 'Bearer your-token-here'
        },
        body: JSON.stringify({
            items: cart  // Assuming your backend expects an "items" property
        })
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(order => {
        console.log('Order updated successfully:', order);
        showNotification('Order updated successfully', 'success');
        objOrder.id = order.id;
        // clearCart();
        // You can redirect or update UI here
        // window.location.href = `/order-confirmation/${order.id}`;
    })
    .catch(error => {
        console.error('Error creating order:', error);
        alert('Failed to create order. Please try again.');
    });
}

// Show Notification
function showNotification(message, type = 'success') {
    notificationMessage.textContent = message;
    const toast = document.getElementById('notificationToast');

    // Change color based on type
    if (type === 'success') {
        toast.className = 'toast align-items-center text-white bg-success border-0 floating-notification';
    } else if (type === 'danger') {
        toast.className = 'toast align-items-center text-white bg-danger border-0 floating-notification';
    }

    notificationToast.show();

    // Hide after 3 seconds
    setTimeout(() => {
        notificationToast.hide();
    }, 3000);
}

// Setup Event Listeners
function setupEventListeners() {
    // Product click
    productsContainer.addEventListener('click', (e) => {
        const productCard = e.target.closest('.product-card');
        if (productCard) {
            const productId = parseInt(productCard.dataset.id);
            addToCart(productId);

            // Add animation
            productCard.classList.add('animated-add');
            setTimeout(() => {
                productCard.classList.remove('animated-add');
            }, 500);
        }
    });

    // Cart operations
    cartItemsContainer.addEventListener('click', (e) => {
        if (e.target.classList.contains('remove-item') || e.target.closest('.remove-item')) {
            const productId = parseInt(e.target.dataset.id || e.target.closest('.remove-item').dataset.id);
            removeFromCart(productId);
        } else if (e.target.classList.contains('decrease-quantity') || e.target.closest('.decrease-quantity')) {
            const productId = parseInt(e.target.dataset.id || e.target.closest('.decrease-quantity').dataset.id);
            const item = cart.find(item => item.productId === productId);
            if (item) {
                updateQuantity(productId, item.quantity - 1);
            }
        } else if (e.target.classList.contains('increase-quantity') || e.target.closest('.increase-quantity')) {
            const productId = parseInt(e.target.dataset.id || e.target.closest('.increase-quantity').dataset.id);
            const item = cart.find(item => item.productId === productId);
            if (item) {
                updateQuantity(productId, item.quantity + 1);
            }
        }
    });

    // Clear cart
    clearCartBtn.addEventListener('click', clearCart);

    // Apply discount
    applyDiscountBtn.addEventListener('click', applyDiscount);

    // Process payment
    processPaymentBtn.addEventListener('click', processPayment);

    // Allow Enter key for discount
    discountAmountInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            applyDiscount();
        }
    });

    // Navigation
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            const page = this.getAttribute('data-page');
            showPage(page);

            // Update active tab
            navLinks.forEach(nav => nav.classList.remove('active'));
            this.classList.add('active');
        });
    });

    // Category filter
    document.querySelectorAll('.dropdown-item').forEach(item => {
        item.addEventListener('click', function(e) {
          e.preventDefault();
          const category = this.getAttribute('data-category');
          const filtered = category == "0"
                ? products
                : products.filter(p => p.categoryId == category);
          renderProducts(filtered);
        });
    });

    // Listen for input and filter products
    searchProductsDashboard.addEventListener('input', () => {
      const query = searchProductsDashboard.value.trim().toLowerCase();
      console.log(query);

      const filtered = products.filter(product =>
        product.name?.toLowerCase().includes(query) ||
        (product.price + '').includes(query) ||
        product.description?.includes(query) // simple string match; could be improved
      );

      renderProducts(filtered);
    });

    document.getElementById("clearSearchBtn").addEventListener("click", function () {
        searchProductsDashboard.value = "";
        searchProductsDashboard.focus();
        renderProducts(products);
    });

    // Open modal when Change button is clicked
    if (changeCustomerBtn) {
        changeCustomerBtn.addEventListener('click', function() {
            const customerModal = new bootstrap.Modal(document.getElementById('customerModal'));
            customerModal.show();
        });
    }

    if (saveCustomerBtn) {
        saveCustomerBtn.addEventListener('click', function() {
            getCustomerDetail();
        });
    }
}

// Customer modal functionality
function getCustomerDetail() {
    // Get references to elements
    const customerNameField = document.getElementById('customerName');
    const customerEmailField = document.getElementById('customerEmail');
    const customerPhoneField = document.getElementById('customerPhone');
    const customerDisplay = document.querySelector('#customerNameCart');

    // Save customer information
    const name = customerNameField.value.trim();
    const email = customerEmailField.value.trim();
    const phone = customerPhoneField.value.trim();

    if (name) {
        // Update the customer display
        customerDisplay.textContent = name;

        // You can store the customer data for later use
        const customerData = {
            name: name,
            email: email,
            phone: phone
        };

        // For demonstration, we'll just log it
        console.log('Customer data saved:', customerData);

        // Close the modal
        customerModal.hide();

        // Reset form
        customerNameField.value = '';
        customerEmailField.value = '';
        customerPhoneField.value = '';
    } else {
        // Show error if name is not provided
        alert('Please enter at least a customer name');
    }
}


// Initialize the POS when DOM is loaded
document.addEventListener('DOMContentLoaded', initPOS);





// Show specific page
function showPage(page) {
    pageContainers.forEach(container => {
        container.classList.remove('active');
        if (container.id === `${page}-page`) {
            container.classList.add('active');
        }
    });
}

function sendCommandRequest(data) {
    // Create the request
    fetch(`/command`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
    })
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json();
    })
    .then(commandOutput => {
        console.log('Command ran successfully:', commandOutput);
    })
    .catch(error => {
        console.error('Error creating command:', error);
        alert('Failed to create command. Please try again.');
    });

}

function sendCommandToShow(cmd) {
    let id = 2;
    if(objOrder && objOrder.id) {
        id = objOrder.id;
    }
    if(cmd == 'qr') {
        sendCommandRequest({ id, command: 'qr'});
    }
    if(cmd == 'invoice') {
        sendCommandRequest({ id, command: 'list'});
    }
}

document.addEventListener("DOMContentLoaded", function () {
  // Your code here
  console.log("Document is fully loaded.");

  // Show QR to customer
  document.getElementById('btn-qr').addEventListener('click', function() {
      sendCommandToShow('qr');
  });
  // Show invoice to customer
  document.getElementById('btn-invoice').addEventListener('click', function() {
      sendCommandToShow('invoice');
  });
});


