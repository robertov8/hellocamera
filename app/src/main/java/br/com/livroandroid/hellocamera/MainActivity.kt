package br.com.livroandroid.hellocamera

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    // Caminho para salvar o arquivo
    var file: File? = null
    val imgView: ImageView by lazy { findViewById<ImageView>(R.id.imagem) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val context = this
        btAbrirCamera.setOnClickListener {
            // (*1*) Criar o caminho do arquivo no sdcard
            // /storage/sdcard/Android/data/br.com.livroandroid.hellocamera/files/Pictures/foto.jpg
            file = getSdCardFile("foto.png")
            Log.d("livro", "Camera file: $file")
            // Chama a intent informando o arquivo para salvar a foto
            val i = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val uri = FileProvider.getUriForFile(context,
                    context.applicationContext.packageName + ".provider", file)
            i.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(i, 0)
        }

        if (savedInstanceState != null) {
            // (*2*) Se girou a tela recupera o estado
            file = savedInstanceState.getSerializable("file") as File
            showImage(file)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // (*3*) Salvar o estado caso gire a tela
        outState.putSerializable("file", file)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("foto", "resultCode: $resultCode")
        if (resultCode == Activity.RESULT_OK) {
            // (*4*) Se a câmera retornou, vamos mostrar o arquivo da foto
            showImage(file)
        }
    }

    // Cria um arquivo no sdcard privado do aplicativo
    private fun getSdCardFile(fileName: String): File {
        val sdCardDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        if (!sdCardDir.exists())
            sdCardDir.mkdir()

        return File(sdCardDir, fileName)
    }

    // Atualiza a imagem na tela
    private fun showImage(file: File?) {
        if (file != null && file.exists()) {
            // (*5*) Redimensiona a imagem para o tamanho do ImageView
            val bitmap = ImageUtils.resize(file, imgView.width, imgView.height)
            toast("w/h: ${imgView.width}/${imgView.height} > w/h: ${bitmap.width}/${bitmap.height}")
            toast("file: $file")
            imgView.setImageBitmap(bitmap)
        }
    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show()
    }
}
