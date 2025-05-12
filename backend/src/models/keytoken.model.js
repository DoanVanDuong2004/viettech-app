const mongoose = require("mongoose");
const DOCUMENT_NAME = 'keytoken'
const COLLECTION_NAME = 'keytokens'
const keyTokenSchema = new mongoose.Schema(
  {
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "account", 
      required: true,
    },
    privateKey: {
      type: String,
      required: true,
    },
    publicKey: {
      type: String,
      required: true,
    },
    refreshTokens: {
      type: [String],
      default: [],
    },
  },
  {  timestamps:true,
    collection:COLLECTION_NAME }
);

module.exports = mongoose.model(DOCUMENT_NAME, keyTokenSchema);
