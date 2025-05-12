import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.datn.viettech_md_12.R

@Composable
fun MyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Color.Black,
    textColor: Color = Color.White,
    painterIconResId: Int? = null,
    vectorIcon: ImageVector? = null,
    enabled: Boolean? = null,
    isLoading: Boolean = false
) {
    Card(
        onClick = { if (!isLoading) onClick() }, //vo hieu hoa khi trang thai load
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor,
            disabledContainerColor = Color.Gray,
            disabledContentColor = Color.Black
        ),
        enabled = enabled ?: true,
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = textColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Text(
                text,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
//                modifier = Modifier
            )
            Spacer(Modifier.width(10.dp))
//            painterIconResId?.let {
//                Image(
//                    modifier = Modifier.size(40.dp),
//                    painter = painterResource(id = it),
//                    contentDescription = "Google Button Icon"
//                )
//            }
            when {
                painterIconResId != null -> {
                    Image(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = painterIconResId),
                        contentDescription = "Button Icon"
                    )
                }
                vectorIcon != null -> {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        imageVector = vectorIcon,
                        contentDescription = "Button Icon",
                        tint = textColor
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewButton() {
    MyButton(
        text = "Click Me",
        onClick = {},
        modifier = Modifier,
        backgroundColor = Color.Black,
        textColor = Color.White,
        painterIconResId = R.drawable.google_logo
    )
}