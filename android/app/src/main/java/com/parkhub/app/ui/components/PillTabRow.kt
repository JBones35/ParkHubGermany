package com.parkhub.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.parkhub.app.model.PillTab
import com.parkhub.app.ui.theme.Gray
import com.parkhub.app.ui.theme.White

@Composable
fun PillTabRow(
    tabs: List<PillTab>,
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    padding: Dp = 0.dp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = padding)
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFFE8E8E8))
            .padding(4.dp)
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = selectedTab == index

            val backgroundColor by animateColorAsState(
                targetValue = if (selected) White else Color.Transparent,
                animationSpec = tween(durationMillis = 150),
                label = "tabBackground"
            )

            val contentColor by animateColorAsState(
                targetValue = if (selected) MaterialTheme.colorScheme.onSurface else Gray,
                animationSpec = tween(durationMillis = 150),
                label = "tabContent"
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .then(
                        if (selected) Modifier.shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(26.dp)
                        ) else Modifier
                    )
                    .background(backgroundColor)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (tab.icon != null) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = null,
                            tint = contentColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = tab.title,
                        fontSize = 14.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = contentColor
                    )
                }
            }
        }
    }
}