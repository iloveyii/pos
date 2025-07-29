// Product Data with Unsplash images
let products = [
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

// Sample orders data
let orders = [
    {
        id: 1001,
        date: "2023-05-15",
        customer: "John Smith",
        items: [
            { productId: 1, name: "Wireless Headphones", price: 99.99, quantity: 1 },
            { productId: 4, name: "USB-C Cable", price: 12.99, quantity: 2 }
        ],
        subtotal: 125.97,
        tax: 10.08,
        discount: 0,
        total: 136.05,
        status: "completed",
        paymentMethod: "Credit Card",
        notes: "Customer requested gift wrapping"
    },
    {
        id: 1002,
        date: "2023-05-16",
        customer: "Sarah Johnson",
        items: [
            { productId: 2, name: "Smart Watch", price: 149.99, quantity: 1 },
            { productId: 6, name: "Laptop Backpack", price: 49.99, quantity: 1 }
        ],
        subtotal: 199.98,
        tax: 16.00,
        discount: 20.00,
        total: 195.98,
        status: "processing",
        paymentMethod: "PayPal",
        notes: "Backorder - will ship when backpack arrives"
    },
    {
        id: 1003,
        date: "2023-05-17",
        customer: "Michael Brown",
        items: [
            { productId: 3, name: "Bluetooth Speaker", price: 59.99, quantity: 3 }
        ],
        subtotal: 179.97,
        tax: 14.40,
        discount: 0,
        total: 194.37,
        status: "pending",
        paymentMethod: "",
        notes: "Customer will pay on pickup"
    },
    {
        id: 1004,
        date: "2023-05-18",
        customer: "Emily Davis",
        items: [
            { productId: 5, name: "Wireless Mouse", price: 24.99, quantity: 2 },
            { productId: 7, name: "Desk Lamp", price: 34.99, quantity: 1 }
        ],
        subtotal: 84.97,
        tax: 6.80,
        discount: 10.00,
        total: 81.77,
        status: "completed",
        paymentMethod: "Cash",
        notes: ""
    },
    {
        id: 1005,
        date: "2023-05-19",
        customer: "Robert Wilson",
        items: [
            { productId: 8, name: "Coffee Mug", price: 14.99, quantity: 4 }
        ],
        subtotal: 59.96,
        tax: 4.80,
        discount: 0,
        total: 64.76,
        status: "cancelled",
        paymentMethod: "",
        notes: "Customer changed mind"
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

        const productsTableBody = document.getElementById('productsTableBody');
        const ordersTableBody = document.getElementById('ordersTableBody');
        const productModal = new bootstrap.Modal(document.getElementById('productModal'));
        const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
        const orderDetailsModal = new bootstrap.Modal(document.getElementById('orderDetailsModal'));
        const pageContainers = document.querySelectorAll('.page-container');
        const navLinks = document.querySelectorAll('.nav-link');
        const filterButtons = document.querySelectorAll('.filter-btn');
        const orderDateFilter = document.getElementById('orderDateFilter');


// Initialize the POS
function initPOS() {
    console.log('initPOS');
    loadProducts();
    renderCart();
    renderOrdersTable();
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
    renderProducts();

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
function renderProducts() {
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
                <div>
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
    } else {
        cart.push({
            productId,
            quantity
        });
    }

    renderCart();
    updateCartOnBackend();
    showNotification(`${quantity} ${quantity > 1 ? 'items' : 'item'} added to cart`);
}

// Remove from Cart
function removeFromCart(productId) {
    cart = cart.filter(item => item.productId !== productId);
    renderCart();
    updateCartOnBackend();
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
        }
    }

    renderCart();
    updateCartOnBackend();
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

function createOrder() {
    // Create the order request
    fetch('/api/orders', {
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

    // Filter buttons for orders
    filterButtons.forEach(btn => {
        btn.addEventListener('click', function() {
            filterButtons.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            const filter = this.getAttribute('data-filter');
            filterOrders(filter);
        });
    });

    // Date filter for orders
    orderDateFilter.addEventListener('change', function() {
        filterOrders('date', this.value);
    });
}

// Initialize the POS when DOM is loaded
document.addEventListener('DOMContentLoaded', initPOS);




// Render orders table
function renderOrdersTable() {
    console.log('renderOrdersTable');
    ordersTableBody.innerHTML = '';

    orders.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>#${order.id}</td>
            <td>${order.date}</td>
            <td>${order.customer}</td>
            <td>${order.items.reduce((sum, item) => sum + item.quantity, 0)}</td>
            <td>$${order.total.toFixed(2)}</td>
            <td>
                <span class="status-badge ${getStatusBadgeClass(order.status)}">
                    ${formatStatus(order.status)}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-primary action-btn view-order" data-id="${order.id}">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-sm btn-outline-secondary action-btn print-order" data-id="${order.id}">
                    <i class="fas fa-print"></i>
                </button>
            </td>
        `;
        ordersTableBody.appendChild(row);
    });

    // Add event listeners to action buttons
    document.querySelectorAll('.view-order').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            viewOrderDetails(orderId);
        });
    });

    document.querySelectorAll('.print-order').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            printOrder(orderId);
        });
    });
}

// Filter orders
function filterOrders(filter, date = '') {
    let filteredOrders = [...orders];

    if (filter === 'pending') {
        filteredOrders = filteredOrders.filter(o => o.status === 'pending');
    } else if (filter === 'processing') {
        filteredOrders = filteredOrders.filter(o => o.status === 'processing');
    } else if (filter === 'completed') {
        filteredOrders = filteredOrders.filter(o => o.status === 'completed');
    } else if (filter === 'cancelled') {
        filteredOrders = filteredOrders.filter(o => o.status === 'cancelled');
    } else if (filter === 'date' && date) {
        filteredOrders = filteredOrders.filter(o => o.date === date);
    }

    // Re-render table with filtered orders
    ordersTableBody.innerHTML = '';

    filteredOrders.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>#${order.id}</td>
            <td>${order.date}</td>
            <td>${order.customer}</td>
            <td>${order.items.reduce((sum, item) => sum + item.quantity, 0)}</td>
            <td>$${order.total.toFixed(2)}</td>
            <td>
                <span class="status-badge ${getStatusBadgeClass(order.status)}">
                    ${formatStatus(order.status)}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-primary action-btn view-order" data-id="${order.id}">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-sm btn-outline-secondary action-btn print-order" data-id="${order.id}">
                    <i class="fas fa-print"></i>
                </button>
            </td>
        `;
        ordersTableBody.appendChild(row);
    });

    // Reattach event listeners
    document.querySelectorAll('.view-order').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            viewOrderDetails(orderId);
        });
    });

    document.querySelectorAll('.print-order').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            printOrder(orderId);
        });
    });
}

// View order details
function viewOrderDetails(orderId) {
    const order = orders.find(o => o.id === orderId);
    if (!order) return;

    // Set order ID in header
    document.getElementById('orderIdHeader').textContent = order.id;

    // Build order details content
    let orderDetails = `
        <div class="order-details-card">
            <div class="order-details-header">
                <div class="row">
                    <div class="col-md-6">
                        <h6 class="fw-bold mb-1">Order #${order.id}</h6>
                        <small class="text-muted">Date: ${order.date}</small>
                    </div>
                    <div class="col-md-6 text-end">
                        <span class="status-badge ${getStatusBadgeClass(order.status)}">
                            ${formatStatus(order.status)}
                        </span>
                    </div>
                </div>
            </div>

            <div class="p-3">
                <div class="mb-4">
                    <h6 class="fw-bold mb-3">Customer Information</h6>
                    <div class="row">
                        <div class="col-md-6">
                            <p class="mb-1"><strong>Name:</strong> ${order.customer}</p>
                        </div>
                        <div class="col-md-6">
                            <p class="mb-1"><strong>Payment Method:</strong> ${order.paymentMethod || 'N/A'}</p>
                        </div>
                    </div>
                </div>

                <h6 class="fw-bold mb-3">Order Items</h6>
    `;

    // Add order items
    order.items.forEach(item => {
        orderDetails += `
            <div class="order-product-item">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="mb-1">${item.name}</h6>
                        <small class="text-muted">$${item.price.toFixed(2)} x ${item.quantity}</small>
                    </div>
                    <div class="text-end">
                        <p class="mb-0 fw-bold">$${(item.price * item.quantity).toFixed(2)}</p>
                    </div>
                </div>
            </div>
        `;
    });

    // Add order summary
    orderDetails += `
            </div>

            <div class="order-summary">
                <div class="d-flex justify-content-between mb-2">
                    <span>Subtotal:</span>
                    <span>$${order.subtotal.toFixed(2)}</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span>Tax (8%):</span>
                    <span>$${order.tax.toFixed(2)}</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span>Discount:</span>
                    <span>-$${order.discount.toFixed(2)}</span>
                </div>
                <hr>
                <div class="d-flex justify-content-between fw-bold">
                    <span>Total:</span>
                    <span>$${order.total.toFixed(2)}</span>
                </div>
            </div>
        </div>
    `;

    // Add notes if available
    if (order.notes) {
        orderDetails += `
            <div class="card mt-3 order-details-card">
                <div class="card-body">
                    <h6 class="fw-bold mb-2">Order Notes</h6>
                    <p class="mb-0">${order.notes}</p>
                </div>
            </div>
        `;
    }

    // Set content and show modal
    document.getElementById('orderDetailsContent').innerHTML = orderDetails;
    orderDetailsModal.show();
}


// Get status badge class
function getStatusBadgeClass(status) {
    switch (status) {
        case 'pending': return 'status-pending';
        case 'processing': return 'status-processing';
        case 'completed': return 'status-completed';
        case 'cancelled': return 'status-cancelled';
        default: return 'status-pending';
    }
}

// Format status for display
function formatStatus(status) {
    return status.charAt(0).toUpperCase() + status.slice(1);
}

// Image upload functionality
document.getElementById('imageUploadContainer').addEventListener('click', function() {
    document.getElementById('productImage').click();
});

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
    if(cmd == 'qr') {
        sendCommandRequest({ id: 2, command: 'qr'});
    }
    if(cmd == 'invoice') {
        sendCommandRequest({ id: 2, command: 'list'});
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


