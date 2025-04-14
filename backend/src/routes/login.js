const express = require("express");
const User = require("../models/User");

const router = express.Router();

router.post("/api/login", async (req, res) => {
  console.log("Login request received with body:", req.body);
  const { email, password } = req.body;

  try {
    const user = await User.findOne({ email, password });
    if (!user) {
      console.log("Invalid credentials for email:", email);
      return res.status(401).json({ message: "Invalid credentials" });
    }

    const userData = {
      _id: user._id,
      name: user.name,
      email: user.email,
      phoneNumber: user.phoneNumber,
      profilePicture: user.profilePicture,
      rating: user.rating,
      totalRatings: user.totalRatings,
    };

    res.status(200).json(userData);
    console.log("User logged in:", userData);
  } catch (err) {
    console.error("Login error:", err);
    res.status(500).json({ message: "Server error" });
  }
});

module.exports = router;
