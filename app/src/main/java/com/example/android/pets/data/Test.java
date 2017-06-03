//package com.example.android.pets.data;
//
//import android.content.ContentProvider;
//import android.content.ContentValues;
//import android.content.UriMatcher;
//import android.database.Cursor;
//import android.net.Uri;
//import android.support.annotation.NonNull;
//import android.support.annotation.Nullable;
//import android.util.Log;
//
///**
// * Created by toluw on 5/27/2017.
// */
//
//public class Test extends ContentProvider {
//
//    PetDbHelper p;
//
//    @Override
//    public boolean onCreate() {
//        p = new PetDbHelper(getContext());
//        int i = 10;
//        String s[] = new String[]{"HEY"}
//
//        switch (i){
//            case 1:
//                Log.d("a", ""+i);
//                Log.d("a", ""+i);
//                break;
//            default:
//                Log.d("a",""+i);
//
//        }
//
//        return false;
//    }
//
//    @Nullable
//    @Override
//    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public String getType(@NonNull Uri uri) {
//        return null;
//    }
//
//    @Nullable
//    @Override
//    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
//        return null;
//    }
//
//    @Override
//    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
//        return 0;
//    }
//
//    @Override
//    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
//        return 0;
//    }
//}
