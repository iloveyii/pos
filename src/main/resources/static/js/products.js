
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

const productsTableBody = document.getElementById('productsTableBody');
// const productDetailsModal = new bootstrap.Modal(document.getElementById('productDetailsModal'));
const productDateFilter = document.getElementById('productDateFilter');
const searchProducts = document.getElementById('searchProducts');
const filterButtonsProducts = document.querySelectorAll('.filter-btn-products');


// Initialize the POS
async function initPOSProducts() {
    console.log('initPOSProducts');
    console.log("Document is fully loaded.");
    const _products = await makeApiRequest('GET', 'products', {});
    console.log('products', _products);
    products = _products;
    renderProductsTable(products);
    addEventListenerForProducts();
}

function addEventListenerForProducts() {
    // Listen for input and filter products
    searchProducts.addEventListener('input', () => {
      const query = searchProducts.value.trim().toLowerCase();
      console.log(query);

      const filtered = products.filter(product =>
        product.name?.toLowerCase().includes(query) ||
        (product.price + '').includes(query) ||
        product.description?.includes(query) // simple string match; could be improved
      );

      renderProductsTable(filtered);
    });

    // Filter buttons for products
    filterButtonsProducts.forEach(btn => {
        btn.addEventListener('click', function() {
            filterButtonsProducts.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            const filter = this.getAttribute('data-filter');
            filterProducts(filter);
        });
    });

    // Date filter for products
    productDateFilter.addEventListener('change', function() {
        filterProducts('date', this.value);
    });

    // Clear btn
    document.getElementById('btnProductsClear').addEventListener('click', function(){
        searchProducts.value = '';
        document.getElementById('allProducts').click();
    });
}


// Render products table
function renderProductsTable(products) {
    console.log('renderProductsTable');
    productsTableBody.innerHTML = '';

    products.forEach(product => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${product.id}</td>
            <td> <img class="img-fluid border rounded p-1 shadow-sm thumb-img" src=${product.image} /> ${product.name}</td>
            <td>${product.description.substring(0,20)}</td>
            <td>$${product.price.toFixed(2)}</td>
            <td>${product.inStock}</td>
            <td>
                <span class="status-badge ${getStatusBadgeClass('active')}">
                    ${formatStatus('active')}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-primary action-btn view-product" data-id="${product.id}">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-sm btn-outline-secondary action-btn print-product" data-id="${product.id}">
                    <i class="fas fa-print"></i>
                </button>
            </td>
        `;
        productsTableBody.appendChild(row);
    });

    // Add event listeners to action buttons
    document.querySelectorAll('.view-product').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            viewProductDetails(orderId);
        });
    });

    document.querySelectorAll('.print-product').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            printOrder(orderId);
        });
    });
}

// Filter products
function filterProducts(filter, date = '') {
    let filteredProducts = [...products];

    if (filter === 'activeProducts') {
        filteredProducts = filteredProducts.filter(p => p.name === 'ACTIVE');
    } else if (filter === 'outOfStockProducts') {
        filteredProducts = filteredProducts.filter(p => p.inStock < 60);
    }

    // Re-render table with filtered products
    productsTableBody.innerHTML = '';
    // Re-render products table
    renderProductsTable(filteredProducts);
    // Reattach event listeners
    document.querySelectorAll('.view-product').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            viewProductDetails(orderId);
        });
    });

    document.querySelectorAll('.print-product').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            printOrder(orderId);
        });
    });
}

// View product details
function viewProductDetails(orderId) {
    const product = products.find(o => o.id === orderId);
    if (!product) return;
    console.log('product', product);

    // Set product ID in header
    document.getElementById('orderIdHeader').textContent = product.id;

    // Build product details content
    let orderDetails = `
        <div class="product-details-card">
            <div class="product-details-header">
                <div class="row">
                    <div class="col-md-6">
                        <h6 class="fw-bold mb-1">product #${product.id}</h6>
                        <small class="text-muted">Date: ${product.date}</small>
                    </div>
                    <div class="col-md-6 text-end">
                        <span class="status-badge ${getStatusBadgeClass(product.status)}">
                            ${formatStatus(product.status)}
                        </span>
                    </div>
                </div>
            </div>

            <div class="p-3">
                <div class="mb-4">
                    <h6 class="fw-bold mb-3">Customer Information</h6>
                    <div class="row">
                        <div class="col-md-6">
                            <p class="mb-1"><strong>Name:</strong> ${product.customer}</p>
                        </div>
                        <div class="col-md-6">
                            <p class="mb-1"><strong>Payment Method:</strong> ${product.paymentMethod || 'N/A'}</p>
                        </div>
                    </div>
                </div>

                <h6 class="fw-bold mb-3">product Items</h6>
    `;

    // Add product summary
    orderDetails += `
            </div>

            <div class="product-summary">
                <div class="d-flex justify-content-between mb-2">
                    <span>Subtotal:</span>
                    <span>$${product.totalAmount.toFixed(2)}</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span>Tax (8%):</span>
                    <span>$${product.totalAmount.toFixed(2)}</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span>Discount:</span>
                    <span>-$${product.discount.toFixed(2)}</span>
                </div>
                <hr>
                <div class="d-flex justify-content-between fw-bold">
                    <span>Total:</span>
                    <span>$${product.totalAmount.toFixed(2)}</span>
                </div>
            </div>
        </div>
    `;

    // Set content and show modal
    document.getElementById('orderDetailsContent').innerHTML = orderDetails;
    // productDetailsModal.show();
}


(async function(){
    console.log("Document is fully loaded.");
    await initPOSProducts();
})();