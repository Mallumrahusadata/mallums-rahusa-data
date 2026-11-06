package com.mallum.rahusadata

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.fragment.app.FragmentActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.mallum.rahusadata.ui.navigation.RahusaNavGraph
import com.mallum.rahusadata.ui.theme.MallumsRahusaDataTheme
import com.mallum.rahusadata.ui.theme.Slate50
import com.mallum.rahusadata.ui.viewmodel.RahusaViewModel

class MainActivity : FragmentActivity() {
    private val viewModel: RahusaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MallumsRahusaDataTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Slate50
                ) {
                    RahusaNavGraph(viewModel = viewModel)
                }
            }
        }
    }
}
