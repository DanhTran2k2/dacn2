const express = require('express');
const router = express.Router();
var sinhvien = require('./controller/sinhvien');

router.post('/sinhvien', sinhvien.post);
module.exports = router;