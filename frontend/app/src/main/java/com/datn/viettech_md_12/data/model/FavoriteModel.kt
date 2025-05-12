import com.google.gson.annotations.SerializedName

data class FavoriteResponse(
    val success: Boolean,
    val favorite: Favorite // Đối tượng "favorite" trả về sau khi thêm sản phẩm thành công
)

data class Favorite(
    @SerializedName("user")
    val user: String,
    @SerializedName("product")
    val product: String,
    @SerializedName("_id")
    val _id: String,
)

data class FavoriteRequest(
    @SerializedName("productId") val productId: String
)
data class FavoriteListResponse(
    val success: Boolean,
    val favorites: List<WishlistItem>
)

// Mỗi mục yêu thích
data class WishlistItem(
    @SerializedName("_id") val id: String,
    val user: String,
    val product: ProductModel02,
    val createdAt: String,
    val updatedAt: String,
    @SerializedName("__v") val version: Int
)

// Thông tin sản phẩm
data class ProductModel02(
    @SerializedName("_id") val id: String,
    val product_name: String,
    val product_thumbnail: String,
    val product_description: String,
    val product_price: Double,
    val product_stock: Int,
    val category: String,
    val product_attributes: ProductAttributes?,
    val product_ratingsAverage: Double,
    val isDraft: Boolean,
    val isPulished: Boolean,
    val variations: List<ProductVariation>,
    val createdAt: String,
    val updatedAt: String,
    val product_slug: String,
    @SerializedName("__v") val version: Int,
    val image_ids: List<String>?,
    val attributeIds: List<String>?
)

// Thuộc tính sản phẩm
data class ProductAttributes(
    val battery_life: String?,
    val camera_quality: String?,
    val screen_size: String?,
    val storage_capacity: String?,
    val ram: String?,
    val operating_system: String?,
    val chipset: String?,
    val processor: String?,
    val graphics_card: String?
)

// Biến thể sản phẩm
data class ProductVariation(
    val variant_name: String,
    val variant_value: String,
    val price: Double,
    val stock: Int,
    val sku: String,
    @SerializedName("_id") val id: String
)