package com.examples.proveedorcontenido_a;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UsuarioProvider extends ContentProvider {
    // DEFINIMOS EL CONTENT_URI
    private static final String uri = "content://com.examples.proveedorcontenido_a/usuario";
    public static final Uri CONTENT_URI = Uri.parse(uri);

    // DEFINIMOS LAS CONSTANTES PARA EL MATCHER
    private static final int USUARIOS = 1;
    private static final int USUARIOS_ID = 2;
    private static final UriMatcher uriMatcher;

    // CREAMOS UNA CLASE INTERNA PARA LAS COLUMNAS
    public static final class Columnas implements BaseColumns {
        private Columnas() {}
        public static final String COL_ID = "_id";
        public static final String COL_USERNAME = "username";
    }

    // CREAMOS LA BASE DE DATOS(DB)
    private UsuarioSQLiteHelper users_db;
    private static final String BD_NAME = "DBusuarios.db";
    private static final int BD_VERSION = 1;
    private static final String TABLE_NAME = "usuarios";

    // EL URI MATCHER
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.examples.proveedorcontenido_a", "usuarios", USUARIOS);
        uriMatcher.addURI("com.examples.proveedorcontenido_a", "usuarios/#", USUARIOS_ID);
    }

    @Override
    public boolean onCreate() {
        // CREAR LA BASE DE DATOS
        users_db = new UsuarioSQLiteHelper(getContext(), BD_NAME, null, BD_VERSION);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        String where = selection;

        // EN CASO DE UN ID EN CONCRETO
        if (uriMatcher.match(uri) == USUARIOS_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        // CONSTRUIR LA CONSULTA
        SQLiteDatabase db = users_db.getWritableDatabase();
        Cursor c = db.query(TABLE_NAME, projection, where, selectionArgs, null, null, sortOrder);

        // Notificar cambios
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case USUARIOS:
                return "vnd.android.cursor.dir/com.examples.usuarios";
            case USUARIOS_ID:
                return "vnd.android.cursor.item/com.examples.usuarios";
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        // Recuperar la base de datos
        SQLiteDatabase db = users_db.getWritableDatabase();
        long regId = db.insert(TABLE_NAME, null, values);
        Uri new_uri = ContentUris.withAppendedId(CONTENT_URI, regId);

        // Notificar cambios
        getContext().getContentResolver().notifyChange(new_uri, null);

        return new_uri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int cont;
        String where = selection;

        // En caso de un ID en concreto
        if (uriMatcher.match(uri) == USUARIOS_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }

        // Ejecutar la eliminación
        SQLiteDatabase db = users_db.getWritableDatabase();
        cont = db.delete(TABLE_NAME, where, selectionArgs);

        // Notificar cambios
        getContext().getContentResolver().notifyChange(uri, null);

        return cont;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int cont;
        String where = selection;

        // En caso de un ID en concreto
        if (uriMatcher.match(uri) == USUARIOS_ID) {
            where = "_id=" + uri.getLastPathSegment();
        }
        SQLiteDatabase db = users_db.getWritableDatabase();
        cont = db.update(TABLE_NAME, values, where, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);

        return cont;
    }
}
