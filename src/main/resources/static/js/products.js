
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
const productModal = new bootstrap.Modal(document.getElementById('productModal'));
const deleteModal = new bootstrap.Modal(document.getElementById('deleteModal'));
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

    // Save product (add/edit)
    document.getElementById('saveProductBtn').addEventListener('click', function() {
        const productId = document.getElementById('productId').value;
        const isEdit = !!productId;

        const productData = {
            name: document.getElementById('productName').value,
            description: document.getElementById('productDescription').value,
            price: parseFloat(document.getElementById('productPrice').value),
            cost: parseFloat(document.getElementById('productCost').value) || 0,
            inStock: parseInt(document.getElementById('productStock').value),
            category: document.getElementById('productCategory').value,
            status: document.getElementById('productStatus').checked,
            barcode: document.getElementById('productBarcode').value,
            weight: parseFloat(document.getElementById('productWeight').value) || 0,
            dimensions: document.getElementById('productDimensions').value,
            image: document.getElementById('imagePreview').src || 'https://via.placeholder.com/200'
        };
        console.log('saveProductBtn', productData);

        if (isEdit) {
            // Update existing product
            const index = products.findIndex(p => p.id === parseInt(productId));
            if (index !== -1) {
                products[index] = {
                    ...products[index],
                    ...productData
                };
                saveProductOnBackend(products[index]);
                showNotification('Product updated successfully');
            }
        } else {
            // Add new product
            const newId = products.length > 0 ? Math.max(...products.map(p => p.id)) + 1 : 1;
            products.unshift({
                id: newId,
                ...productData
            });
            saveProductOnBackend(productData);
            showNotification('Product added successfully');
        }

        // Update views and close modal
        // renderProducts(); // for POS cards
        renderProductsTable(products);
        productModal.hide();
    });

    // Image upload functionality
    document.getElementById('imageUploadContainer').addEventListener('click', function() {
        document.getElementById('productImage').click();
    });
    document.getElementById('productImage').addEventListener('change', function(e) {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = function(event) {
                const imagePreview = document.getElementById('imagePreview');
                imagePreview.src = event.target.result;
                imagePreview.style.display = 'block';
            };
            reader.readAsDataURL(file);
        }
    });
}

function saveProductOnBackend(productData) {
    if(productData.id)
        makeApiRequest('PUT', `products/${productData.id}`, productData);
    else
        makeApiRequest('POST', `products`, productData);
}

function addEventListenerForProductRowsActions() {
    // Add event listeners to action buttons
    document.querySelectorAll('.edit-product').forEach(btn => {
        btn.addEventListener('click', function() {
            const productId = parseInt(this.getAttribute('data-id'));
            editProduct(productId);
        });
    });

    document.querySelectorAll('.delete-product').forEach(btn => {
        btn.addEventListener('click', function() {
            const productId = parseInt(this.getAttribute('data-id'));
            confirmDelete(productId);
        });
    });

    // Delete product
    document.getElementById('confirmDeleteBtn').addEventListener('click', function() {
        const productId = parseInt(this.getAttribute('data-id'));
        products = products.filter(p => p.id !== productId);

        // Update views and close modal
        // renderProducts();
        renderProductsTable(products);
        deleteModal.hide();
        showNotification('Product deleted successfully');
    });
}

// Render products table
function renderProductsTable2(products) {
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
                <span class="status-badge ${getStatusBadgeClass(product.status)}">
                    ${formatStatus(product.status)}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-primary action-btn edit-product" data-id="${product.id}">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger action-btn delete-product" data-id="${product.id}">
                    <i class="fas fa-trash-alt"></i>
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

    addEventListenerForProductRowsActions();
}


// Render products table with pagination
function renderProductsTable(products, pageInfo = null) {
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
                <span class="status-badge ${getStatusBadgeClass(product.status)}">
                    ${formatStatus(product.status)}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-primary action-btn edit-product" data-id="${product.id}">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-danger action-btn delete-product" data-id="${product.id}">
                    <i class="fas fa-trash-alt"></i>
                </button>
            </td>
        `;
        productsTableBody.appendChild(row);
    });

    // Render pagination controls
    renderPaginationControls(pageInfo);
}

// Render pagination controls
function renderPaginationControls(pageInfo = null) {
    pageInfo = {
        number: 0,        // Current page number
        size: 3,            // Page size
        totalElements: 3, // Total items
        totalPages: 4, // Total pages
        first: true,          // Is first page?
        last: false             // Is last page?
    };
    // Remove existing pagination if it exists
    const existingPagination = document.getElementById('productsPagination');
    if (existingPagination) {
        existingPagination.remove();
    }

    // Create pagination container
    const paginationContainer = document.createElement('div');
    paginationContainer.id = 'productsPagination';
    paginationContainer.className = 'd-flex justify-content-between align-items-center mt-4';

    // Add pagination info (showing X to Y of Z items)
    const paginationInfo = document.createElement('div');
    paginationInfo.className = 'text-muted';

    const startItem = (pageInfo.number * pageInfo.size) + 1;
    const endItem = Math.min((pageInfo.number + 1) * pageInfo.size, pageInfo.totalElements);

    paginationInfo.innerHTML = `Showing ${startItem} to ${endItem} of ${pageInfo.totalElements} entries`;

    // Create pagination navigation
    const paginationNav = document.createElement('nav');
    paginationNav.setAttribute('aria-label', 'Products pagination');

    const paginationList = document.createElement('ul');
    paginationList.className = 'pagination mb-0';

    // Previous button
    const prevLi = document.createElement('li');
    prevLi.className = `page-item ${pageInfo.first ? 'disabled' : ''}`;
    prevLi.innerHTML = `
        <a class="page-link" href="#" data-page="${pageInfo.number - 1}">
            <i class="fas fa-chevron-left"></i>
        </a>
    `;
    paginationList.appendChild(prevLi);

    // Page numbers
    const totalPages = pageInfo.totalPages;
    const currentPage = pageInfo.number;

    // Show limited page numbers with ellipsis for many pages
    let startPage = Math.max(0, currentPage - 2);
    let endPage = Math.min(totalPages - 1, currentPage + 2);

    // Adjust if we're near the start
    if (currentPage < 3) {
        endPage = Math.min(4, totalPages - 1);
    }

    // Adjust if we're near the end
    if (currentPage > totalPages - 4) {
        startPage = Math.max(0, totalPages - 5);
    }

    // First page and ellipsis if needed
    if (startPage > 0) {
        const firstLi = document.createElement('li');
        firstLi.className = 'page-item';
        firstLi.innerHTML = `<a class="page-link" href="#" data-page="0">1</a>`;
        paginationList.appendChild(firstLi);

        if (startPage > 1) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = `<span class="page-link">...</span>`;
            paginationList.appendChild(ellipsisLi);
        }
    }

    // Page number buttons
    for (let i = startPage; i <= endPage; i++) {
        const pageLi = document.createElement('li');
        pageLi.className = `page-item ${i === currentPage ? 'active' : ''}`;
        pageLi.innerHTML = `<a class="page-link" href="#" data-page="${i}">${i + 1}</a>`;
        paginationList.appendChild(pageLi);
    }

    // Last page and ellipsis if needed
    if (endPage < totalPages - 1) {
        if (endPage < totalPages - 2) {
            const ellipsisLi = document.createElement('li');
            ellipsisLi.className = 'page-item disabled';
            ellipsisLi.innerHTML = `<span class="page-link">...</span>`;
            paginationList.appendChild(ellipsisLi);
        }

        const lastLi = document.createElement('li');
        lastLi.className = 'page-item';
        lastLi.innerHTML = `<a class="page-link" href="#" data-page="${totalPages - 1}">${totalPages}</a>`;
        paginationList.appendChild(lastLi);
    }

    // Next button
    const nextLi = document.createElement('li');
    nextLi.className = `page-item ${pageInfo.last ? 'disabled' : ''}`;
    nextLi.innerHTML = `
        <a class="page-link" href="#" data-page="${pageInfo.number + 1}">
            <i class="fas fa-chevron-right"></i>
        </a>
    `;
    paginationList.appendChild(nextLi);

    paginationNav.appendChild(paginationList);

    // Assemble the pagination container
    paginationContainer.appendChild(paginationInfo);
    paginationContainer.appendChild(paginationNav);

    // Add pagination after the table
    const tableContainer = productsTableBody.closest('.table-responsive');
    tableContainer.parentNode.insertBefore(paginationContainer, tableContainer.nextSibling);

    // Add event listeners to pagination buttons
    attachPaginationEventListeners();
}

function fetchProducts(page = 0, size = 10, sortBy = 'name', sortDir = 'asc') {
    // Update global variables
    currentPage = page;
    currentPageSize = size;
    currentSortBy = sortBy;
    currentSortDir = sortDir;
}

// Attach event listeners to pagination buttons
function attachPaginationEventListeners() {
    const paginationLinks = document.querySelectorAll('#productsPagination .page-link:not(.disabled)');

    paginationLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            const page = parseInt(this.getAttribute('data-page'));
            if (!isNaN(page)) {
                // Call your function to fetch products for the selected page
                fetchProducts(page, currentPageSize, currentSortBy, currentSortDir);
            }
        });
    });
}


// Filter products
function filterProducts(filter, date = '') {
    let filteredProducts = [...products];

    if (filter === 'activeProducts') {
        filteredProducts = filteredProducts.filter(p => p.status === true);
    } else if(filter === 'inActiveProducts') {
        filteredProducts = filteredProducts.filter(p => p.status === false);
    }
    else if (filter === 'outOfStockProducts') {
        filteredProducts = filteredProducts.filter(p => p.inStock < 60);
    } else if(filter === 'date' && date) {
         filteredProducts = filteredProducts.filter(p => p.updatedAt?.substring(0,10) === date);
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

// Edit product
function editProduct(productId) {
    const product = products.find(p => p.id === productId);
    if (!product) return;

    // Set modal title
    document.getElementById('modalTitle').textContent = 'Edit Product';

    // Fill form with product data
    document.getElementById('productId').value = product.id;
    document.getElementById('productName').value = product.name;
    document.getElementById('productDescription').value = product.description;
    document.getElementById('productPrice').value = product.price;
    document.getElementById('productCost').value = product.price;
    document.getElementById('productStock').value = product.inStock;
    document.getElementById('productCategory').value = product.categoryId;
    document.getElementById('productStatus').checked = product.status;
    document.getElementById('productBarcode').value = product.barcode || '';
    document.getElementById('productWeight').value = product.weight || '';
    document.getElementById('productDimensions').value = product.dimensions || '';

    // Set image preview
    const imagePreview = document.getElementById('imagePreview');
    if (product.image) {
        imagePreview.src = product.image;
        imagePreview.style.display = 'block';
    } else {
        imagePreview.style.display = 'none';
    }

    // Show modal
    productModal.show();
}

// Confirm product deletion
function confirmDelete(productId) {
    document.getElementById('confirmDeleteBtn').setAttribute('data-id', productId);
    deleteModal.show();
}

// Get status badge class
function getStatusBadgeClass(status) {
    if(status === true)
        return 'status-completed';
    if(status === false)
        return 'status-processing';
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
    console.log('status:' + status);
    status = status === true ? 'active' : 'inactive';
    return status.charAt(0).toUpperCase() + status.slice(1);
}

(async function(){
    console.log("Document is fully loaded.");
    await initPOSProducts();
})();