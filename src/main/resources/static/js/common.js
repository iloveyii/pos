// API request
async function makeApiRequest(httpMethod, endPoint, data) {
    // Make the request to /api/endpoint
    if(httpMethod === 'GET' && Object.keys(data).length > 0) {
        Object.keys(data).map( k => endPoint = endPoint + `/${data[k]}`)
    }
    return await fetch('/api/' + endPoint, {
        method: httpMethod,
        headers: {
             'Content-Type': 'application/json',
             "Authorization": "Bearer " + localStorage.getItem("jwt")
        },
        body: httpMethod === 'GET'? null : JSON.stringify(data)
    })
    .then(response => {
        // Check if response is successful (status code 200-299)
        if (!response.ok) {
          throw new Error(`HTTP error! status: ${response.status}`);
        }

        if(response) {
            const contentType = response.headers.get("content-type");
            if (contentType && contentType.includes("application/json"))
                return response.json(); // Parse JSON response
            return {};
        }
        else
            return {};
    })
    .then(items => {
        // Success - log products to console
        console.log('API response Items:', items);
        return items;
    })
    .catch(error => {
        // Error handling
        console.error('Error fetching products:', error);
        window.location.href = "/auth/login";
        // Show user-friendly message
        // const shouldRefresh = confirm('Failed to load products. Click OK to login');
        // if (shouldRefresh) {
            // window.location.href = "/auth/login";
            // window.location.reload(); // Refresh the page
        //}
    });
}

function loadOrders() {
    console.log('loadOrders');
    var script = document.createElement('script');
    script.src = '/js/orders.js?samgo';
    document.body.appendChild(script);
}


// Render pagination controls
function renderPaginationControlsInCommon(pageInfo, tableBody, containerId, fetchItems) {
    console.log('renderPaginationControlsInCommon pageinfo', pageInfo);
    // Remove existing pagination if it exists
    const existingPagination = document.getElementById(containerId);
    if (existingPagination) {
        existingPagination.remove();
    }

    // Create pagination container
    const paginationContainer = document.createElement('div');
    paginationContainer.id = containerId;
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
    const tableContainer = tableBody.closest('.table-responsive');
    tableContainer.parentNode.insertBefore(paginationContainer, tableContainer.nextSibling);

    // Add event listeners to pagination buttons
    attachPaginationEventListenersInCommon(pageInfo, containerId, fetchItems);
}

// Attach event listeners to pagination buttons
function attachPaginationEventListenersInCommon(pageInfo, containerId, fetchItems) {
    const paginationLinks = document.querySelectorAll(`#${containerId} .page-link:not(.disabled)`);

    paginationLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();

            const page = parseInt(this.getAttribute('data-page'));
            if (!isNaN(page)) {
                // Call your function to fetch products for the selected page
                pageInfo.number = page;
                fetchItems(page, currentPageSize, currentSortBy, currentSortDir);
            }
        });
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

function showWaiting() {
    const showWaitingBtn = document.getElementById('showWaitingBtn');
    const waitingModal = document.getElementById('waitingModal');

    // Show the modal
    const modal = new bootstrap.Modal(waitingModal);
    modal.show();
    // Hide after 3 seconds
    setTimeout(function() {
        modal.hide();
        if(modal._isShown)
            window.location.href = '/auth/login';
    }, 15000);
    return modal;
}


function base64urlToUint8Array(base64url) {
    const padding = '='.repeat((4 - base64url.length % 4) % 4);
    const base64 = (base64url + padding)
        .replace(/-/g, '+')
        .replace(/_/g, '/');
    const rawData = atob(base64);
    const outputArray = new Uint8Array(rawData.length);
    for (let i = 0; i < rawData.length; ++i) {
        outputArray[i] = rawData.charCodeAt(i);
    }
    return outputArray;
}

async function fileExistsOnServer(url, errorDivId) {
    return fetch(url, { method: "HEAD" })
    .then(res => {
      if (res.ok) {
        document.getElementById(errorDivId).innerHTML =
                  "<p style='color:green;'>✅ PDF file found.</p>";
                  return true;
      } else {
        document.getElementById(errorDivId).innerHTML =
          "<p style='color:red;'>❌ PDF file not found.</p>";
          return false;
      }
    })
    .catch((e) => {
      document.getElementById(errorDivId).innerHTML =
        "<p style='color:red;'>⚠️ Error loading PDF.</p>" + e.message;
        console.log(e);
        return false;
    });
}