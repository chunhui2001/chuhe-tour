const PROXY_CONFIG = [
  {
    context: [
      "/access_token.json"
    ],
    target: "http://localhost:8081",
    secure: false,
    bypass: function (req, res, proxyOptions) {

      /* if (req.headers.accept.indexOf("html") !== -1) {
        console.log("Skipping proxy for browser request.");
        return "/index.html";
      }
      
      req.headers["X-Custom-Header"] = "yes"; */

    }
  }
]

module.exports = PROXY_CONFIG;