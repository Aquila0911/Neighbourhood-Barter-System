const express = require("express");
const User = require("../models/User");

const router = express.Router();

router.post("/api/update", async (req, res) => {
  console.log("Update Request Received:", req.body);

  try {
    const { _id, name, email, phoneNumber } = req.body;

    const updatedUser = await User.findByIdAndUpdate(
      _id,
      { name, email, phoneNumber },
      { new: true }
    );

    console.log("Updated user:", updatedUser);
    res.status(200).json(updatedUser);
  } catch (error) {
    res.status(500).json({ message: "Server error", error: error.message });
  }
});

module.exports = router;
