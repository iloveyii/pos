
// Sample orders data
let orders2 = [
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

const ordersTableBody2 = document.getElementById('ordersTableBody');
const orderDetailsModal2 = new bootstrap.Modal(document.getElementById('orderDetailsModal'));
const orderDateFilter2 = document.getElementById('orderDateFilter');

// Initialize the POS
function initPOS2() {
    console.log('initPOS');
    renderOrdersTable();
    setupEventListeners();
}

document.addEventListener("DOMContentLoaded", async function () {
    console.log("Document is fully loaded.");
    const orders = await makeApiRequest('GET', 'orders', {});
});

(async function(){
    console.log("Document is fully loaded.");
    const orders = await makeApiRequest('GET', 'orders', {});
    console.log('orders', orders);
})();