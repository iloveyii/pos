// API request
async function makeApiRequest(httpMethod, endPoint, data) {
    // Make the request to /api/endpoint
    if(httpMethod === 'GET' && Object.keys(data).length > 0) {
        Object.keys(data).map( k => endPoint = endPoint + `/${data[k]}`)
    }
    return await fetch('/api/' + endPoint, {
        method: httpMethod,
        headers: {
             'Content-Type': 'application/json'
        },
        body: httpMethod === 'GET'? null : JSON.stringify(data)
    })
    .then(response => {
        // Check if response is successful (status code 200-299)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }
        return response.json(); // Parse JSON response
    })
    .then(items => {
        // Success - log products to console
        console.log('API response Items:', items);
        return items;
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

function loadOrders() {
    console.log('loadOrders');
    var script = document.createElement('script');
    script.src = '/js/orders.js?samgo';
    document.body.appendChild(script);
}