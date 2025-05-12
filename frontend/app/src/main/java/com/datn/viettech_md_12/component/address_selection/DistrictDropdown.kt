package com.datn.viettech_md_12.component.address_selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.model.content.RectangleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictDropdown(
    selectedDistrict: String,
    selectedProvince: String,
    onDistrictSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    // Map các quận/huyện theo tỉnh/thành phố
    val districtsByProvince = mapOf(
        "Hà Nội" to listOf(
            "Ba Đình",
            "Hoàn Kiếm",
            "Tây Hồ",
            "Cầu Giấy",
            "Đống Đa",
            "Hai Bà Trưng",
            "Hoàng Mai",
            "Thanh Xuân",
            "Long Biên",
            "Nam Từ Liêm",
            "Bắc Từ Liêm",
            "Hà Đông",
            "Sơn Tây",
            "Ba Vì",
            "Chương Mỹ",
            "Đan Phượng",
            "Đông Anh",
            "Gia Lâm",
            "Hoài Đức",
            "Mê Linh",
            "Mỹ Đức",
            "Phú Xuyên",
            "Phúc Thọ",
            "Quốc Oai",
            "Sóc Sơn",
            "Thạch Thất",
            "Thanh Oai",
            "Thanh Trì",
            "Thường Tín",
            "Ứng Hòa"
        ),
        "TP Hồ Chí Minh" to listOf(
            "Quận 1",
            "Quận 2",
            "Quận 3",
            "Quận 4",
            "Quận 5",
            "Quận 6",
            "Quận 7",
            "Quận 8",
            "Quận 9",
            "Quận 10",
            "Quận 11",
            "Quận 12",
            "Bình Tân",
            "Bình Thạnh",
            "Gò Vấp",
            "Phú Nhuận",
            "Tân Bình",
            "Tân Phú",
            "Thủ Đức",
            "Bình Chánh",
            "Cần Giờ",
            "Củ Chi",
            "Hóc Môn",
            "Nhà Bè"
        ),
        "Đà Nẵng" to listOf(
            "Hải Châu",
            "Thanh Khê",
            "Sơn Trà",
            "Ngũ Hành Sơn",
            "Cẩm Lệ",
            "Liên Chiểu",
            "Hòa Vang",
            "Hoàng Sa"
        ),
        "Hải Phòng" to listOf(
            "Hồng Bàng",
            "Ngô Quyền",
            "Lê Chân",
            "Kiến An",
            "Đồ Sơn",
            "Dương Kinh",
            "Hải An",
            "An Dương",
            "An Lão",
            "Bạch Long Vĩ",
            "Cát Hải",
            "Kiến Thụy",
            "Thủy Nguyên",
            "Tiên Lãng",
            "Vĩnh Bảo"
        ),
        "Cần Thơ" to listOf(
            "Ninh Kiều",
            "Bình Thủy",
            "Cái Răng",
            "Ô Môn",
            "Thốt Nốt",
            "Vĩnh Thạnh",
            "Cờ Đỏ",
            "Phong Điền",
            "Thới Lai"
        ),
        "An Giang" to listOf(
            "Long Xuyên",
            "Châu Đốc",
            "An Phú",
            "Châu Phú",
            "Châu Thành",
            "Chợ Mới",
            "Phú Tân",
            "Tân Châu",
            "Thoại Sơn",
            "Tịnh Biên",
            "Tri Tôn"
        ),
        "Bà Rịa - Vũng Tàu" to listOf(
            "Bà Rịa",
            "Vũng Tàu",
            "Châu Đức",
            "Côn Đảo",
            "Đất Đỏ",
            "Long Điền",
            "Tân Thành",
            "Xuyên Mộc"
        ),
        "Bạc Liêu" to listOf(
            "Bạc Liêu",
            "Đông Hải",
            "Giá Rai",
            "Hòa Bình",
            "Hồng Dân",
            "Phước Long",
            "Vĩnh Lợi"
        ),
        "Bắc Giang" to listOf(
            "Bắc Giang",
            "Hiệp Hòa",
            "Lạng Giang",
            "Lục Nam",
            "Lục Ngạn",
            "Sơn Động",
            "Tân Yên",
            "Việt Yên",
            "Yên Dũng",
            "Yên Thế"
        ),
        "Bắc Kạn" to listOf(
            "Bắc Kạn",
            "Ba Bể",
            "Bạch Thông",
            "Chợ Đồn",
            "Chợ Mới",
            "Na Rì",
            "Ngân Sơn",
            "Pác Nặm"
        ),
        "Bắc Ninh" to listOf(
            "Bắc Ninh",
            "Gia Bình",
            "Lương Tài",
            "Quế Võ",
            "Thuận Thành",
            "Tiên Du",
            "Từ Sơn",
            "Yên Phong"
        ),
        "Bến Tre" to listOf(
            "Bến Tre",
            "Ba Tri",
            "Bình Đại",
            "Châu Thành",
            "Chợ Lách",
            "Giồng Trôm",
            "Mỏ Cày Bắc",
            "Mỏ Cày Nam",
            "Thạnh Phú"
        ),
        "Bình Định" to listOf(
            "Quy Nhơn",
            "An Lão",
            "An Nhơn",
            "Hoài Ân",
            "Hoài Nhơn",
            "Phù Cát",
            "Phù Mỹ",
            "Tây Sơn",
            "Tuy Phước",
            "Vân Canh",
            "Vĩnh Thạnh"
        ),
        "Bình Dương" to listOf(
            "Thủ Dầu Một",
            "Bàu Bàng",
            "Bến Cát",
            "Dầu Tiếng",
            "Phú Giáo",
            "Tân Uyên",
            "Thuận An",
            "Bắc Tân Uyên"
        ),
        "Bình Phước" to listOf(
            "Đồng Xoài",
            "Bình Long",
            "Chơn Thành",
            "Đồng Phú",
            "Hớn Quản",
            "Lộc Ninh",
            "Phú Riềng",
            "Bù Đăng",
            "Bù Đốp",
            "Bù Gia Mập"
        ),
        "Bình Thuận" to listOf(
            "Phan Thiết",
            "La Gi",
            "Bắc Bình",
            "Đức Linh",
            "Hàm Thuận Bắc",
            "Hàm Thuận Nam",
            "Hàm Tân",
            "Phú Quý",
            "Tánh Linh",
            "Tuy Phong"
        ),
        "Cà Mau" to listOf(
            "Cà Mau",
            "Cái Nước",
            "Đầm Dơi",
            "Năm Căn",
            "Ngọc Hiển",
            "Phú Tân",
            "Thới Bình",
            "Trần Văn Thời",
            "U Minh"
        ),
        "Cao Bằng" to listOf(
            "Cao Bằng",
            "Bảo Lạc",
            "Bảo Lâm",
            "Hạ Lang",
            "Hà Quảng",
            "Hòa An",
            "Nguyên Bình",
            "Phục Hòa",
            "Quảng Hòa",
            "Thạch An",
            "Trùng Khánh"
        ),
        "Đắk Lắk" to listOf(
            "Buôn Ma Thuột",
            "Buôn Đôn",
            "Cư Kuin",
            "Cư M'gar",
            "Ea H'leo",
            "Ea Kar",
            "Ea Súp",
            "Krông Ana",
            "Krông Bông",
            "Krông Búk",
            "Krông Năng",
            "Krông Pắk",
            "Lắk",
            "M'Đrắk"
        ),
        "Đắk Nông" to listOf(
            "Gia Nghĩa",
            "Cư Jút",
            "Đắk Glong",
            "Đắk Mil",
            "Đắk R'Lấp",
            "Đắk Song",
            "Krông Nô",
            "Tuy Đức"
        ),
        "Điện Biên" to listOf(
            "Điện Biên Phủ",
            "Mường Lay",
            "Điện Biên",
            "Điện Biên Đông",
            "Mường Ảng",
            "Mường Chà",
            "Mường Nhé",
            "Nậm Pồ",
            "Tủa Chùa",
            "Tuần Giáo"
        ),
        "Đồng Nai" to listOf(
            "Biên Hòa",
            "Cẩm Mỹ",
            "Định Quán",
            "Long Khánh",
            "Long Thành",
            "Nhơn Trạch",
            "Tân Phú",
            "Thống Nhất",
            "Trảng Bom",
            "Vĩnh Cửu",
            "Xuân Lộc"
        ),
        "Đồng Tháp" to listOf(
            "Cao Lãnh",
            "Sa Đéc",
            "Châu Thành",
            "Hồng Ngự",
            "Lai Vung",
            "Lấp Vò",
            "Tam Nông",
            "Tân Hồng",
            "Thanh Bình",
            "Tháp Mười"
        ),
        "Gia Lai" to listOf(
            "Pleiku",
            "An Khê",
            "Ayun Pa",
            "Chư Păh",
            "Chư Prông",
            "Chư Sê",
            "Đắk Đoa",
            "Đắk Pơ",
            "Đức Cơ",
            "Ia Grai",
            "Ia Pa",
            "KBang",
            "Kông Chro",
            "Krông Pa",
            "Mang Yang",
            "Phú Thiện"
        ),
        "Hà Giang" to listOf(
            "Hà Giang",
            "Bắc Mê",
            "Bắc Quang",
            "Đồng Văn",
            "Hoàng Su Phì",
            "Mèo Vạc",
            "Quản Bạ",
            "Quang Bình",
            "Vị Xuyên",
            "Xín Mần",
            "Yên Minh"
        ),
        "Hà Nam" to listOf("Phủ Lý", "Bình Lục", "Duy Tiên", "Kim Bảng", "Lý Nhân", "Thanh Liêm"),
        "Hà Tĩnh" to listOf(
            "Hà Tĩnh",
            "Cẩm Xuyên",
            "Can Lộc",
            "Đức Thọ",
            "Hương Khê",
            "Hương Sơn",
            "Kỳ Anh",
            "Lộc Hà",
            "Nghi Xuân",
            "Thạch Hà",
            "Vũ Quang"
        ),
        "Hải Dương" to listOf(
            "Hải Dương",
            "Bình Giang",
            "Cẩm Giàng",
            "Gia Lộc",
            "Kim Thành",
            "Kinh Môn",
            "Nam Sách",
            "Ninh Giang",
            "Thanh Hà",
            "Thanh Miện",
            "Tứ Kỳ"
        ),
        "Hậu Giang" to listOf(
            "Vị Thanh",
            "Châu Thành",
            "Châu Thành A",
            "Long Mỹ",
            "Ngã Bảy",
            "Phụng Hiệp",
            "Vị Thủy"
        ),
        "Hòa Bình" to listOf(
            "Hòa Bình",
            "Cao Phong",
            "Đà Bắc",
            "Kim Bôi",
            "Lạc Sơn",
            "Lạc Thủy",
            "Lương Sơn",
            "Mai Châu",
            "Tân Lạc",
            "Yên Thủy"
        ),
        "Hưng Yên" to listOf(
            "Hưng Yên",
            "Ân Thi",
            "Khoái Châu",
            "Kim Động",
            "Phù Cừ",
            "Tiên Lữ",
            "Văn Giang",
            "Văn Lâm",
            "Yên Mỹ"
        ),
        "Khánh Hòa" to listOf(
            "Nha Trang",
            "Cam Lâm",
            "Cam Ranh",
            "Diên Khánh",
            "Khánh Sơn",
            "Khánh Vĩnh",
            "Ninh Hòa",
            "Trường Sa",
            "Vạn Ninh"
        ),
        "Kiên Giang" to listOf(
            "Rạch Giá",
            "An Biên",
            "An Minh",
            "Châu Thành",
            "Giang Thành",
            "Giồng Riềng",
            "Gò Quao",
            "Hà Tiên",
            "Hòn Đất",
            "Kiên Hải",
            "Kiên Lương",
            "Phú Quốc",
            "Tân Hiệp",
            "U Minh Thượng",
            "Vĩnh Thuận"
        ),
        "Kon Tum" to listOf(
            "Kon Tum",
            "Đắk Glei",
            "Đắk Hà",
            "Đắk Tô",
            "Ia H'Drai",
            "Kon Plông",
            "Kon Rẫy",
            "Ngọc Hồi",
            "Sa Thầy",
            "Tu Mơ Rông"
        ),
        "Lai Châu" to listOf(
            "Lai Châu",
            "Mường Tè",
            "Nậm Nhùn",
            "Phong Thổ",
            "Sìn Hồ",
            "Tam Đường",
            "Tân Uyên",
            "Than Uyên"
        ),
        "Lâm Đồng" to listOf(
            "Đà Lạt",
            "Bảo Lâm",
            "Bảo Lộc",
            "Cát Tiên",
            "Đạ Huoai",
            "Đạ Tẻh",
            "Đam Rông",
            "Di Linh",
            "Đơn Dương",
            "Đức Trọng",
            "Lạc Dương",
            "Lâm Hà"
        ),
        "Lạng Sơn" to listOf(
            "Lạng Sơn",
            "Bắc Sơn",
            "Bình Gia",
            "Cao Lộc",
            "Chi Lăng",
            "Đình Lập",
            "Hữu Lũng",
            "Lộc Bình",
            "Tràng Định",
            "Văn Lãng",
            "Văn Quan"
        ),
        "Lào Cai" to listOf(
            "Lào Cai",
            "Bắc Hà",
            "Bảo Thắng",
            "Bảo Yên",
            "Bát Xát",
            "Mường Khương",
            "Sa Pa",
            "Si Ma Cai",
            "Văn Bàn"
        ),
        "Long An" to listOf(
            "Tân An",
            "Bến Lức",
            "Cần Đước",
            "Cần Giuộc",
            "Châu Thành",
            "Đức Hòa",
            "Đức Huệ",
            "Kiến Tường",
            "Mộc Hóa",
            "Tân Hưng",
            "Tân Thạnh",
            "Tân Trụ",
            "Thạnh Hóa",
            "Thủ Thừa",
            "Vĩnh Hưng"
        ),
        "Nam Định" to listOf(
            "Nam Định",
            "Giao Thủy",
            "Hải Hậu",
            "Mỹ Lộc",
            "Nam Trực",
            "Nghĩa Hưng",
            "Trực Ninh",
            "Vụ Bản",
            "Xuân Trường",
            "Ý Yên"
        ),
        "Nghệ An" to listOf(
            "Vinh",
            "Anh Sơn",
            "Con Cuông",
            "Cửa Lò",
            "Diễn Châu",
            "Đô Lương",
            "Hoàng Mai",
            "Hưng Nguyên",
            "Kỳ Sơn",
            "Nam Đàn",
            "Nghi Lộc",
            "Nghĩa Đàn",
            "Quế Phong",
            "Quỳ Châu",
            "Quỳ Hợp",
            "Quỳnh Lưu",
            "Tân Kỳ",
            "Thái Hòa",
            "Thanh Chương",
            "Tương Dương",
            "Vinh",
            "Yên Thành"
        ),
        "Ninh Bình" to listOf(
            "Ninh Bình",
            "Gia Viễn",
            "Hoa Lư",
            "Kim Sơn",
            "Nho Quan",
            "Tam Điệp",
            "Yên Khánh",
            "Yên Mô"
        ),
        "Ninh Thuận" to listOf(
            "Phan Rang – Tháp Chàm",
            "Bác Ái",
            "Ninh Hải",
            "Ninh Phước",
            "Ninh Sơn",
            "Thuận Bắc",
            "Thuận Nam"
        ),
        "Phú Thọ" to listOf(
            "Việt Trì",
            "Cẩm Khê",
            "Đoan Hùng",
            "Hạ Hòa",
            "Lâm Thao",
            "Phù Ninh",
            "Tam Nông",
            "Tân Sơn",
            "Thanh Ba",
            "Thanh Sơn",
            "Thanh Thủy",
            "Yên Lập"
        ),
        "Phú Yên" to listOf(
            "Tuy Hòa",
            "Đông Hòa",
            "Đồng Xuân",
            "Phú Hòa",
            "Sơn Hòa",
            "Sông Cầu",
            "Sông Hinh",
            "Tây Hòa",
            "Tuy An"
        ),
        "Quảng Bình" to listOf(
            "Đồng Hới",
            "Bố Trạch",
            "Lệ Thủy",
            "Minh Hóa",
            "Quảng Ninh",
            "Quảng Trạch",
            "Tuyên Hóa"
        ),
        "Quảng Nam" to listOf(
            "Tam Kỳ",
            "Hội An",
            "Bắc Trà My",
            "Đại Lộc",
            "Điện Bàn",
            "Đông Giang",
            "Duy Xuyên",
            "Hiệp Đức",
            "Nam Giang",
            "Nam Trà My",
            "Núi Thành",
            "Phú Ninh",
            "Phước Sơn",
            "Quế Sơn",
            "Tây Giang",
            "Thăng Bình",
            "Tiên Phước"
        ),
        "Quảng Ngãi" to listOf(
            "Quảng Ngãi",
            "Ba Tơ",
            "Bình Sơn",
            "Đức Phổ",
            "Lý Sơn",
            "Minh Long",
            "Mộ Đức",
            "Nghĩa Hành",
            "Sơn Hà",
            "Sơn Tây",
            "Sơn Tịnh",
            "Trà Bồng",
            "Tư Nghĩa",
            "Lý Sơn"
        ),
        "Quảng Ninh" to listOf(
            "Hạ Long",
            "Móng Cái",
            "Cẩm Phả",
            "Uông Bí",
            "Bình Liêu",
            "Đầm Hà",
            "Hải Hà",
            "Tiên Yên",
            "Ba Chẽ",
            "Vân Đồn",
            "Hoành Bồ",
            "Đông Triều",
            "Quảng Yên",
            "Cô Tô"
        ),
        "Quảng Trị" to listOf(
            "Đông Hà",
            "Quảng Trị",
            "Cam Lộ",
            "Cồn Cỏ",
            "Đa Krông",
            "Gio Linh",
            "Hải Lăng",
            "Hướng Hóa",
            "Triệu Phong",
            "Vĩnh Linh"
        ),
        "Sóc Trăng" to listOf(
            "Sóc Trăng",
            "Châu Thành",
            "Cù Lao Dung",
            "Kế Sách",
            "Long Phú",
            "Mỹ Tú",
            "Mỹ Xuyên",
            "Ngã Năm",
            "Thạnh Trị",
            "Trần Đề",
            "Vĩnh Châu"
        ),
        "Sơn La" to listOf(
            "Sơn La",
            "Bắc Yên",
            "Mai Sơn",
            "Mộc Châu",
            "Mường La",
            "Phù Yên",
            "Quỳnh Nhai",
            "Sông Mã",
            "Sốp Cộp",
            "Thuận Châu",
            "Vân Hồ",
            "Yên Châu"
        ),
        "Tây Ninh" to listOf(
            "Tây Ninh",
            "Bến Cầu",
            "Châu Thành",
            "Dương Minh Châu",
            "Gò Dầu",
            "Hòa Thành",
            "Trảng Bàng",
            "Tân Biên",
            "Tân Châu"
        ),
        "Thái Bình" to listOf(
            "Thái Bình",
            "Đông Hưng",
            "Hưng Hà",
            "Kiến Xương",
            "Quỳnh Phụ",
            "Thái Thụy",
            "Tiền Hải",
            "Vũ Thư"
        ),
        "Thái Nguyên" to listOf(
            "Thái Nguyên",
            "Đại Từ",
            "Định Hóa",
            "Đồng Hỷ",
            "Phổ Yên",
            "Phú Bình",
            "Phú Lương",
            "Sơn Dương",
            "Võ Nhai"
        ),
        "Thanh Hóa" to listOf(
            "Thanh Hóa",
            "Bá Thước",
            "Cẩm Thủy",
            "Đông Sơn",
            "Hà Trung",
            "Hậu Lộc",
            "Hoằng Hóa",
            "Lang Chánh",
            "Mường Lát",
            "Nga Sơn",
            "Ngọc Lặc",
            "Như Thanh",
            "Như Xuân",
            "Nông Cống",
            "Quan Hóa",
            "Quan Sơn",
            "Quảng Xương",
            "Thạch Thành",
            "Thiệu Hóa",
            "Thọ Xuân",
            "Thường Xuân",
            "Tĩnh Gia",
            "Triệu Sơn",
            "Vĩnh Lộc",
            "Yên Định"
        ),
        "Thừa Thiên Huế" to listOf(
            "Huế",
            "A Lưới",
            "Nam Đông",
            "Phong Điền",
            "Phú Lộc",
            "Phú Vang",
            "Quảng Điền"
        ),
        "Tiền Giang" to listOf(
            "Mỹ Tho",
            "Cái Bè",
            "Cai Lậy",
            "Châu Thành",
            "Chợ Gạo",
            "Gò Công",
            "Gò Công Đông",
            "Gò Công Tây",
            "Tân Phú Đông",
            "Tân Phước"
        ),
        "Trà Vinh" to listOf(
            "Trà Vinh",
            "Càng Long",
            "Châu Thành",
            "Cầu Kè",
            "Cầu Ngang",
            "Duyên Hải",
            "Tiểu Cần",
            "Trà Cú"
        ),
        "Tuyên Quang" to listOf(
            "Tuyên Quang",
            "Chiêm Hóa",
            "Hàm Yên",
            "Lâm Bình",
            "Na Hang",
            "Sơn Dương",
            "Yên Sơn"
        ),
        "Vĩnh Long" to listOf(
            "Vĩnh Long",
            "Bình Minh",
            "Bình Tân",
            "Long Hồ",
            "Mang Thít",
            "Tam Bình",
            "Trà Ôn",
            "Vũng Liêm"
        ),
        "Vĩnh Phúc" to listOf(
            "Vĩnh Yên",
            "Bình Xuyên",
            "Lập Thạch",
            "Phúc Yên",
            "Sông Lô",
            "Tam Đảo",
            "Tam Dương",
            "Vĩnh Tường",
            "Yên Lạc"
        ),
        "Yên Bái" to listOf(
            "Yên Bái",
            "Lục Yên",
            "Mù Căng Chải",
            "Nghĩa Lộ",
            "Trạm Tấu",
            "Trấn Yên",
            "Văn Chấn",
            "Văn Yên",
            "Yên Bình"
        )
    )

    // Danh sách quận/huyện theo tỉnh/thành đã chọn
    val districts = districtsByProvince[selectedProvince] ?: emptyList()

    // Lọc danh sách dựa trên từ khóa tìm kiếm
    val filteredDistricts = remember(searchText, selectedProvince) {
        if (searchText.isEmpty()) {
            districts // Nếu không tìm kiếm, hiển thị toàn bộ danh sách
        } else {
            districts.filter { it.contains(searchText, ignoreCase = true) }
        }
    }

    Column(modifier = modifier) {
        OutlinedTextField(
            value = selectedDistrict,
            onValueChange = {},
            readOnly = true,
            label = { Text("Quận/Huyện") },
            trailingIcon = {
                IconButton(onClick = { expanded = !expanded }) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                        contentDescription = "Dropdown", tint = Color.Black
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent, shape = RoundedCornerShape(8.dp))
                .clickable { expanded = true },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color.White,
                focusedBorderColor = Color(0xFF21D4B4),
                unfocusedBorderColor = Color(0xFFC9C9D3),
                focusedLabelColor = Color(0xFF21D4B4),
                unfocusedLabelColor = Color.Black,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                disabledTextColor = Color.Gray,
            ),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 300.dp)
                .background(Color(0xFFF5F5F8))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
            ){
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(8.dp)),
                    placeholder = { Text("Tìm kiếm...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    singleLine = true,
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        focusedBorderColor = Color(0xFF21D4B4),
                        unfocusedBorderColor = Color(0xFFC9C9D3),
                        focusedLabelColor = Color.Black,
                        unfocusedLabelColor = Color.Black,
                        focusedPlaceholderColor = Color.Black,
                        unfocusedPlaceholderColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        disabledTextColor = Color.Gray,
                    )
                )
            }

            filteredDistricts.forEach { district ->
                DropdownMenuItem(
                    text = {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(
                                    border = BorderStroke(width = 0.5.dp, color = Color.LightGray),
                                    shape = RectangleShape
                                )
                                .padding(10.dp)
                        ) {
                            Text(district, color = Color.Black)
                        }
                    },
                    onClick = {
                        onDistrictSelected(district)
                        expanded = false
                        searchText = ""
                    }
                )
            }

            if (filteredDistricts.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("Không có dữ liệu", color = Color.Black) },
                    onClick = {}
                )
            }
        }
    }
}


@Preview(showSystemUi = true)
@Composable
fun DistrictDropdownPreview() {
    val selectedDistrict by remember { mutableStateOf("Hà Nội") }
    val selectedProvince by remember { mutableStateOf("Hà Nội") }

    DistrictDropdown(
        selectedDistrict,
        selectedProvince,
        onDistrictSelected = {},
    )
}