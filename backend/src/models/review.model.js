const mongoose = require("mongoose");

const DOCUMENT_NAME = "review";
const COLLECTION_NAME = "reviews";

const ReviewSchema = new mongoose.Schema(
  {
    account_id: { type: mongoose.Schema.Types.ObjectId, ref: "account", required: true },
    product_id: { type: mongoose.Schema.Types.ObjectId, ref: "Product", required: true },
    contents_review: { type: String, required: true },
    image_ids: [{ type: mongoose.Schema.Types.ObjectId, ref: "Image" }],
    rating: { type: Number, required: true },
    bill_id: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "bill" 
    },
    status: {
      type: String,
      enum: ["active", "inactive"],
      default: "active"
    }
  },
  {
    timestamps: true,
    collection: COLLECTION_NAME
  }
);

module.exports = mongoose.model(DOCUMENT_NAME, ReviewSchema);
