package com.examples.proveedorcontenido_a;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Verificar y solicitar permisos
        if (checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 1);
        } else {
            mostrarContactos();
        }
    }

    private void mostrarContactos() {
        // Proyección de las columnas que quieres recuperar
        String[] projection = new String[]{
                ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME
        };

        // URI para acceder a los contactos
        Uri contactosUri = ContactsContract.Contacts.CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();

        // Recuperar el cursor
        Cursor c = contentResolver.query(contactosUri, projection, null, null, null);
        StringBuilder cadena = new StringBuilder();  // Usar StringBuilder para eficiencia

        if (c != null && c.moveToFirst()) {
            do {
                // Obtener el nombre de contacto
                @SuppressLint("Range") String nombre = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                cadena.append("Contacto: ").append(nombre).append("\n");
            } while (c.moveToNext());

            // Mostrar resultados en el TextView
            TextView txtContactos = findViewById(R.id.textoUsuarios);
            txtContactos.setText(cadena.toString());

            // Cerrar el cursor
            c.close();
        } else {
            // No se encontraron contactos
            TextView txtContactos = findViewById(R.id.textoUsuarios);
            txtContactos.setText("No se encontraron contactos.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            mostrarContactos();
        } else {
            // Manejo si el permiso no es concedido
            TextView txtContactos = findViewById(R.id.textoUsuarios);
            txtContactos.setText("Permiso para acceder a contactos denegado.");
        }
    }
}
