
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

const ordersTableBody = document.getElementById('ordersTableBody');
const orderDetailsModal = new bootstrap.Modal(document.getElementById('orderDetailsModal'));
const orderDateFilter = document.getElementById('orderDateFilter');
const searchOrders = document.getElementById('searchOrders');
const filterButtonsOrders = document.querySelectorAll('.filter-btn-orders');
const printOrderBtn = document.querySelector('#printOrderBtn');


const pdfModal = new bootstrap.Modal(document.getElementById('pdfModal'));
const pdfFrame = document.getElementById('pdfFrame');
const pdfLoading = document.getElementById('pdfLoading');
const pdfOrderId = document.getElementById('pdfOrderId');
const downloadPdf = document.getElementById('downloadPdf');

// Global variables to track current pagination state
let currentPageOrders = 0;
let currentPageSizeOrders = 10;
let currentSortByOrders = 'name';
let currentSortDirOrders = 'asc';

let pageInfoOrders = {
    number: 0,        // Current page number
    size: 3,            // Page size
    totalElements: 3, // Total items
    totalPages: 4, // Total pages
    first: true,          // Is first page?
    last: false             // Is last page?
};

function updatePageInfoOrders(pageProducts) {
    pageInfoOrders = {
        number: pageProducts.pageable.pageNumber,        // Current page number
        size: pageProducts.pageable.pageSize,            // Page size
        totalElements: pageProducts.totalElements, // Total items
        totalPages: pageProducts.totalPages, // Total pages
        first: pageProducts.first,          // Is first page?
        last: pageProducts.last             // Is last page?
    };
}

async function fetchOrders(page = 0, size = 15, sortBy = 'name', sortDir = 'asc') {
    // Update global variables
    currentPageOrders = page;
    currentPageSizeOrders = size;
    currentSortByOrders = sortBy;
    currentSortDirOrders = sortDir;
    console.log( 'pagination:', {
        currentPageOrders,
        currentPageSizeOrders,
        currentSortByOrders,
        currentSortDirOrders
    });
    const pageOrders = await makeApiRequest('GET', 'orders', {page: currentPageOrders, size:5});
    orders = pageOrders.content;
    console.log('orders', pageOrders);
    updatePageInfoOrders(pageOrders);
    renderOrdersTable(pageOrders.content);
}

// Initialize the POS
async function initPOSOrders() {
    console.log('initPOSOrders');
    await fetchOrders();
    renderOrdersTable(orders);
    addEventListenerForOrders();
}

function addEventListenerForOrders() {
    // Listen for input and filter orders
    searchOrders.addEventListener('input', () => {
      const query = searchOrders.value.trim().toLowerCase();
      console.log(query);

      const filtered = orders.filter(order =>
        order.notes?.toLowerCase().includes(query) ||
        (order.totalAmount + '').includes(query) ||
        order.orderDateString?.includes(query) ||
        (order.id + '').includes(query)
      );

      renderOrdersTable(filtered);
    });

    // Filter buttons for orders
    filterButtonsOrders.forEach(btn => {
        btn.addEventListener('click', function() {
            filterButtonsOrders.forEach(b => b.classList.remove('active'));
            this.classList.add('active');
            const filter = this.getAttribute('data-filter');
            filterOrders(filter);
        });
    });

    // Date filter for orders
    orderDateFilter.addEventListener('change', function() {
        filterOrders('date', this.value);
    });

    // print order
    printOrderBtn.addEventListener('click', function() {
        const orderId = parseInt(this.getAttribute('data-id'));
        console.log('printOrderBtn data id: ' + orderId);
        printOrder(orderId);
    });

    // Clear btn
    document.getElementById('btnOrdersClear').addEventListener('click', function(){
        searchOrders.value = '';
        document.getElementById('allOrders').click();
    });

}

function addEventListenerForOrderRowsActions(){
    // Add event listeners to action buttons
    document.querySelectorAll('.edit-order').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            console.log('view cart for order: ' + orderId);
            const order = orders.find(o => o.id === orderId);
            if (!order) return;
            console.log('Order found:: ', order);
            objOrder.id = order.id;
            cart = order.orderProducts.map(o => ({productId: o.productId, quantity: o.quantity}));
            console.log('Show cart::', cart);
            showPage('pos');
            renderCart();
        });
    });

    document.querySelectorAll('.print-order').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            printOrder(orderId);
        });
    });

    document.querySelectorAll('.pdf-view-receipt').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            openPdfModal(orderId);
        });
    });

    document.querySelectorAll('.view-order').forEach(btn => {
        btn.addEventListener('click', function() {
            const orderId = parseInt(this.getAttribute('data-id'));
            console.log('view order: ' + orderId);
            viewOrderDetails(orderId);
        });
    });
}

// Render orders table
function renderOrdersTable(orders) {
    console.log('renderOrdersTable');
    ordersTableBody.innerHTML = '';

    orders.forEach(order => {
        const row = document.createElement('tr');
        row.innerHTML = `
            <td>${order.id}</td>
            <td>${order.orderDateString}</td>
            <td>${order.orderProducts.reduce((sum, item) => sum + item.quantity, 0)}</td>
            <td>$${order.totalAmount.toFixed(2)}</td>
            <td>
                <span class="status-badge ${getStatusBadgeClassForOrders(order.status)}">
                    ${formatStatusOrders(order.status)}
                </span>
            </td>
            <td>
                <button class="btn btn-sm btn-outline-primary action-btn edit-order" data-id="${order.id}">
                    <i class="fas fa-edit"></i>
                </button>
                <button class="btn btn-sm btn-outline-primary action-btn view-order" data-id="${order.id}">
                    <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-sm btn-outline-secondary action-btn print-order" data-id="${order.id}">
                    <i class="fas fa-print"></i>
                </button>
                <button class="btn btn-sm btn-outline-secondary action-btn pdf-view-receipt" data-id="${order.id}">
                    <i class="fas fa-file-pdf"></i>
                </button>
            </td>
        `;
        ordersTableBody.appendChild(row);
    });

    renderPaginationControlsInCommon(pageInfoOrders, ordersTableBody, 'ordersPagination', (page, currentPageSize, currentSortBy, currentSortDir)=>fetchOrders(page, currentPageSize, currentSortBy, currentSortDir));
    addEventListenerForOrderRowsActions();
}

// Filter orders
function filterOrders(filter, date = '') {
    let filteredOrders = [...orders];

   if (filter === 'date' && date) {
        filteredOrders = filteredOrders.filter(o => o.orderDateString.substring(0,10) === date);
    } else if(filter === 'all'){
        filteredOrders = [...orders];
    } else {
        filteredOrders = filteredOrders.filter(o => o.status === filter.toUpperCase());
    }

    // Re-render table with filtered orders
    ordersTableBody.innerHTML = '';
    // Re-render orders table
    renderOrdersTable(filteredOrders);
    // Reattach event listeners
    addEventListenerForOrderRowsActions();
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
                        <span class="status-badge ${getStatusBadgeClassForOrders(order.status)}">
                            ${formatStatusOrders(order.status)}
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
    order.orderProducts.forEach(item => {
        orderDetails += `
            <div class="order-product-item">
                <div class="d-flex justify-content-between">
                    <div>
                        <h6 class="mb-1">${item.productName}</h6>
                        <small class="text-muted">$${item.priceAtPurchase.toFixed(2)} x ${item.quantity}</small>
                    </div>
                    <div class="text-end">
                        <p class="mb-0 fw-bold">$${(item.priceAtPurchase * item.quantity).toFixed(2)}</p>
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
                    <span>$${order.totalAmount.toFixed(2)}</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span>Tax (8%):</span>
                    <span>$${order.totalAmount.toFixed(2)}</span>
                </div>
                <div class="d-flex justify-content-between mb-2">
                    <span>Discount:</span>
                    <span>-$${order.discount.toFixed(2)}</span>
                </div>
                <hr>
                <div class="d-flex justify-content-between fw-bold">
                    <span>Total:</span>
                    <span>$${order.totalAmount.toFixed(2)}</span>
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
    // data-id for print button
    console.log('setting dataset.id for printOrderBtn = ' + order.id);
    printOrderBtn.dataset.id = order.id;
    orderDetailsModal.show();
}

function printOrder(id) {
    console.log('printing order: ' + id);
    // Send order for printing to ws
    sendCommandRequest({ id, command: 'print'});
}

// Get status badge class
function getStatusBadgeClassForOrders(status) {
    status = status.toLowerCase();
    return `status-${status}`;
}
// Format status for display
function formatStatusOrders(status) {
    return status.charAt(0).toUpperCase() + status.slice(1);
}

// PDF Modal functionality
document.addEventListener('DOMContentLoaded', function() {
    // Reset modal when closed
    document.getElementById('pdfModal').addEventListener('hidden.bs.modal', function() {
        pdfFrame.src = 'about:blank';
    });
});

// Function to open PDF modal
async function openPdfModal(orderId) {
    const pdfUrl = `https://pos.softhem.net/pdf/${orderId}`;
    // Set modal title
    pdfOrderId.textContent = orderId;

    // Set download link
    downloadPdf.href = pdfUrl;
    downloadPdf.setAttribute('download', `order-${orderId}.pdf`);

    // Show loading state
    pdfLoading.style.display = 'flex';
    pdfFrame.style.display = 'none';

    // Show modal
    pdfModal.show();

    const fileExists = await fileExistsOnServer('https://pos.softhem.net/pdf/4', 'pdfLoading');
    if(fileExists) {
        showPdfInFrame(pdfUrl);
    } else {
        pdfLoading.style.display = 'block';
        pdfFrame.style.display = 'none';
        setTimeout(() => showPdfInFrame(pdfUrl), 300);
    }
}

function showPdfInFrame(pdfUrl) {
// Load PDF after a short delay to allow modal to animate
    setTimeout(() => {
        pdfFrame.src = pdfUrl;

        // Handle when PDF is loaded
        pdfFrame.onload = function() {
            pdfLoading.style.display = 'none';
            pdfFrame.style.display = 'block';
        };

        // Handle PDF loading errors
        pdfFrame.onerror = function() {
            pdfLoading.style.display = 'block';
            pdfFrame.style.display = 'none';
            pdfLoading.innerHTML = `
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-triangle me-2"></i>
                    Failed to load PDF document. Please try again later.
                </div>
            `;
        };
    }, 500);
}


(async function(){
    console.log("Document is fully loaded.");
    await initPOSOrders();
})();