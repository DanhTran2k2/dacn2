var mqtt = require("mqtt");
options = {
    clientId: "danh",
    username: "danhtran98",
    password: "GvrLmUyR",
    clean: true
};

var client = mqtt.connect('mqtt://ngoinhaiot.com:1111', options);
client.on('connect', () => {
    console.log("mqtt connected")
    client.subscribe("danhtran98/vku/b203")
  })

module.exports = client