const express = require("express");
const Item = require("../models/Item");
const User = require("../models/User");

const router = express.Router();

// Create new item
router.post("/api/createItem", async (req, res) => {
  const { name, description, image, category, ownerId } = req.body;

  try {
    const user = await User.findById(ownerId);
    if (!user) {
      return res.status(404).json({ message: "User not found" });
    }

    // Create the item
    const newItem = await Item.create({
      name,
      description,
      image,
      condition,
      category,
      owner: ownerId,
    });

    // Add item to user's itemsListed
    user.itemsListed.push(newItem._id);
    await user.save();

    res.status(201).json(newItem);
  } catch (err) {
    console.error("Error creating item:", err);
    res.status(500).json({ message: "Server error" });
  }
});

module.exports = router;
