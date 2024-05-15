package com.ssafy.stab.screens.note

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.ssafy.stab.R
import com.ssafy.stab.components.note.ColorOptions
import com.ssafy.stab.components.note.ControlsBar
import com.ssafy.stab.components.note.PageInterfaceBar
import com.ssafy.stab.components.note.PageList
import com.ssafy.stab.components.note.StrokeOptions
import com.ssafy.stab.ui.theme.Background
import com.ssafy.stab.util.note.NoteControlViewModel

@Composable
fun PersonalNote(
    noteViewModel: NoteViewModel,
    navController: NavController
){
    val noteControlViewModel : NoteControlViewModel = viewModel()

    val undoAvailable by noteControlViewModel.undoAvailable.collectAsState()
    val redoAvailable by noteControlViewModel.redoAvailable.collectAsState()

    val currentPage = remember { mutableIntStateOf(0) }
    val onPageChange = { page: Int -> currentPage.intValue = page }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .background(Color(0xFFB9CDFF))
                .height(52.dp)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row {
                Image(
                    painterResource(id = R.drawable.left),
                    "back",
                    Modifier
                        .size(36.dp)
                        .clickable {
                            noteViewModel.savePage(noteControlViewModel.pathList)
                            navController.popBackStack()
                        }
                )
                Spacer(modifier = Modifier.width(8.dp))
                val noteTitle by noteViewModel.noteTitle.collectAsState()
                Text(
                    text = noteTitle,
                    modifier = Modifier
                        .align(Alignment.CenterVertically),
                    fontSize = 28.sp,
                    fontWeight = FontWeight.W500
                )
            }
            PageInterfaceBar(
                currentPage = currentPage.intValue,
                viewModel = noteViewModel,
            )
        }

        Row(
            modifier = Modifier
                .background(Background)
                .height(52.dp)
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ControlsBar(noteControlViewModel, undoAvailable, redoAvailable,)
            Divider(
                color = Color(0xFFCCD7ED),
                modifier = Modifier.height(28.dp).width(2.dp)
            )
            ColorOptions(noteControlViewModel)
            Divider(
                color = Color(0xFFCCD7ED),
                modifier = Modifier.height(28.dp).width(2.dp)
            )
            StrokeOptions(noteControlViewModel)
        }

        PageList(noteViewModel, noteControlViewModel, onPageChange)
    }
}