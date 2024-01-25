package com.wishes.jetpackcompose.utlis

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TabPosition
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.dp

class ViewUtils {


    companion object{
        fun Modifier.customTabIndicatorOffset(
            currentTabPosition: TabPosition
        ): Modifier = composed(
            inspectorInfo = debugInspectorInfo {
                name = "tabIndicatorOffset"
                value = currentTabPosition
            }
        ) {
            val indicatorWidth = 30.dp
            val indicatorHeight = 3.dp
            val currentTabWidth = currentTabPosition.width
            val indicatorOffset by animateDpAsState(
                targetValue = currentTabPosition.left + currentTabWidth / 2 - indicatorWidth / 2,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessMedium,
                )
            )
            fillMaxWidth()
                .wrapContentSize(Alignment.BottomStart)
                .offset(x = indicatorOffset)
                .clip(RoundedCornerShape(4.dp))
                .width(indicatorWidth)
                .height(indicatorHeight)
        }
    }
}