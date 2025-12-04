package com.example.smarttalk.screens

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.smarttalk.ui.theme.theme_blue
import com.example.smarttalk.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController
){
    Scaffold(
        topBar = {
            TopAppBar(
                windowInsets = TopAppBarDefaults.windowInsets,
                title = { Text("Profile") },
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = theme_blue,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "back",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

            )
        }
    ){ innerPadding ->

        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.height(60.dp))
            ProfileImageWithEdit(
                "https://lipsum.app/random/1600x900",
                "ddvbl",
                {}
            )
            Spacer(modifier = Modifier.height(25.dp))
            settingsWidget(
                heading = "Name",
                data = "Raghav",
                drawableRes = R.drawable.ic_user
            )
            Divider(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp))
            settingsWidget(
                heading = "Mobile",
                data = "+91 8437837835",
                drawableRes = R.drawable.ic_phone
            )
            Divider(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp))

        }

    }
}

@Composable
fun ProfileImageWithEdit(
    imageUrl: String?,
    selectedImageUri: String?,
    onEditClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(130.dp), // Size of the profile image
        contentAlignment = Alignment.Center
    ) {
        // Profile Image
        AsyncImage(
            model = imageUrl,
            contentDescription = "Profile Image",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .border(2.dp, Color.Gray, CircleShape),
            contentScale = ContentScale.Crop
        )

        // Edit Icon (bottom-right)
        IconButton(
            onClick = onEditClick,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .background(Color.White, CircleShape)
                .border(1.dp, Color.LightGray, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Profile Picture",
                tint = Color.Black,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


@Composable
fun profileImagePicker() {

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var showDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }

    // Launcher for camera
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        bitmap?.let {
            val uri = saveBitmapToCache(context, it)
            imageUri = uri
        }
    } as (Bitmap?) -> Unit

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Choose Image") },
            text = { Text("Select image source") },
            confirmButton = {
                Column {
                    TextButton(onClick = {
                        showDialog = false
                        cameraLauncher.launch(null)
                    }) {
                        Text("Camera")
                    }
                    TextButton(onClick = {
                        showDialog = false
                        galleryLauncher.launch("image/*")
                    }) {
                        Text("Gallery")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

}


@Composable
fun settingsWidget(
    heading : String,
    data : String,
    drawableRes: Int
){

    Row(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image( painter = painterResource(drawableRes),
            contentDescription = "Description of image",
            modifier = Modifier.size(35.dp) )

        Column(
            modifier = Modifier.fillMaxWidth()
                .padding(start = 20.dp)
        ) {
            Text(
                text = heading,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = data,
                fontSize = 16.sp
            )
        }

    }



}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): Uri {
    val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
    file.outputStream().use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
}



