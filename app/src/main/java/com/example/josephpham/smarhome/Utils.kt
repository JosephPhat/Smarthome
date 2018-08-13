package com.example.josephpham.smarhome

import android.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso
import android.widget.EditText
import de.hdodenhof.circleimageview.CircleImageView


class Utils {
    companion object {
        @JvmStatic @BindingAdapter("imageUrl", "error")
        fun loadImage(view: CircleImageView, url:String, err: Drawable) {
            Picasso.get().load(url).error(err).into(view)
        }
        @JvmStatic @BindingAdapter("imageViewUrl", "error")
        fun loadImageView(view: ImageView, url:String, err: Drawable) {
            Picasso.get().load(url).error(err).into(view)
        }
    }
}
