// Import required modules
const http = require('http');
const { exec } = require('child_process');
const url = require('url');
const fs = require('fs');
const path = require('path');
const { setTimeout } = require('timers/promises');


// Create a basic HTTP server
const server = http.createServer((req, res) => {
  const parsedUrl = url.parse(req.url, true);
  const pathname = parsedUrl.pathname;

  if(req.url === '/generate' && req.method === 'POST') {
    let body = '';
    // Collect the request data
    req.on('data', chunk => body += chunk.toString());
    
    req.on('end', async() => {
      try {
        const jsonData = JSON.parse(body);
        console.log('Received JSON:', jsonData); // Log to console
        // await compose_latex_main_file(jsonData.order);
        // await create_dir(jsonData.order);
        // const pdf_url = await generate_pdf(jsonData.order);
        // console.log('pdf_url:', pdf_url);
        
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ 
          status: 'success', 
          // data: { url: pdf_url }
        }));
      } catch (e) {
        res.writeHead(400, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ 
          status: 'error', 
          message: 'Invalid JSON' 
        }));
      }
    });
  } else {
    // Handle 404 for other routes
    res.writeHead(404, { 'Content-Type': 'text/plain' });
    res.end('404 Not Found - No route\n');
  }
});

// Start the server on port 3000
const PORT = 3001;
const HOST = '0.0.0.0'; // Listen on all network interfaces
server.listen(PORT, HOST, () => {
  console.log(`Server running at http://localhost:${PORT}/`);
});