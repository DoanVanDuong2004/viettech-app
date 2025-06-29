const mongoose = require("mongoose");
const DOCUMENT_NAME = 'account';
const COLLECTION_NAME = 'accounts';
const DEFAULT_PROFILE_IMAGE_ID = "67d3b37b63838e785e7844da"; // ID ảnh mặc định

const AccountSchema = new mongoose.Schema(
    {
        username: { type: String, required: true, unique: true },
        full_name: { type: String, required: true },
        phone: { type: String, required: true, unique: true },
        address: { type: String, required: true },
        email: { type: String, required: true, unique: true },
        profile_image: {
            type: mongoose.Schema.Types.ObjectId,
            ref: "Image",
            default: DEFAULT_PROFILE_IMAGE_ID 
        }, password: { type: String, required: true },
        role_id: { type: mongoose.Schema.Types.ObjectId, ref: "role", required: true },
        status: {
            type: String,
            enum: ['active', 'inactive', 'suspended'], // Những trạng thái có thể có
            default: 'active', // Mặc định là active
            required: true
        },
        oneSignalId: {
            type: String,
            default: null
        },
    },
    {
        timestamps: true,
        collection: COLLECTION_NAME
    }
);

module.exports = mongoose.model(DOCUMENT_NAME, AccountSchema);
