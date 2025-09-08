// Import required modules
const http = require('http');
const { exec } = require('child_process');
const url = require('url');
const fs = require('fs');
const path = require('path');
const { setTimeout } = require('timers/promises');


const generate_pdf_invoice = async(order) => {
    console.log('order in generate_pdf_invoice', order);
  if(order && order.id) {
    console.log('order id:' + order.id);
    await setTimeout(1000);
    // xelatex -output-directory=/data/pdf/9 -jobname=9 /data/tex/9.tex
    const command = `xelatex -output-directory=/data/pdf/${order.id} -jobname=${order.id} /data/tex/${order.id}.tex`;
    console.log('command::' + command);
    // Execute the command
    exec(command, (error, stdout, stderr) => {
      if (error) {
        console.log(`Error: ${error.message}`);
        return error.message;
      }
      if (stderr) {
        console.log(`Stderr: ${stderr}`);
        return stderr;
      }

      // Send the command output
      console.log(`Command Output in generate_pdf_invoice:\n${stdout}`);
    });

    return `/pdf/${order.id}/${order.id}.pdf`;
  } else {
    console.log(`Command Output in generate_pdf_invoice: no order.id found \n`);
    return "no order.id found";
  }
}

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
        console.log('Received JSON new:', jsonData); // Log to console
        const pdf_url = await generate_pdf_invoice(jsonData);

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ 
          status: 'success', 
          data: { url: pdf_url }
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