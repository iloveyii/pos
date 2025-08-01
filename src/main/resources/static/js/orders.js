
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

// Initialize the POS
function initPOSOrders() {
    console.log('initPOSOrders');
    renderOrdersTable(orders);
}

document.addEventListener("DOMContentLoaded", async function () {
    console.log("Document is fully loaded.");
    const orders = await makeApiRequest('GET', 'orders', {});
});

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
        filteredOrders = filteredOrders.filter(o => o.status === 'PENDING');
    } else if (filter === 'processing') {
        filteredOrders = filteredOrders.filter(o => o.status === 'PROCESSING');
    } else if (filter === 'completed') {
        filteredOrders = filteredOrders.filter(o => o.status === 'COMPLETED');
    } else if (filter === 'cancelled') {
        filteredOrders = filteredOrders.filter(o => o.status === 'CANCELLED');
    } else if (filter === 'date' && date) {
        filteredOrders = filteredOrders.filter(o => o.date === date);
    }

    // Re-render table with filtered orders
    ordersTableBody.innerHTML = '';
    // Re-render orders table
    renderOrdersTable(filteredOrders);
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
    orderDetailsModal.show();
}


(async function(){
    console.log("Document is fully loaded.");
    const _orders = await makeApiRequest('GET', 'orders', {});
    console.log('orders', _orders);
    orders = _orders;
    initPOSOrders();
})();