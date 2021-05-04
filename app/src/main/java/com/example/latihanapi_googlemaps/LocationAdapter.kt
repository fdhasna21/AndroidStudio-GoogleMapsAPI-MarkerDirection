package com.example.latihanapi_googlemaps

import android.content.Context
import android.location.Geocoder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText

class LocationAdapter(val context: Context, val arrayList: ArrayList<LocationModel>)
    : RecyclerView.Adapter<LocationAdapter.ViewHolder>(){

    class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        val layoutAdapt = itemView.findViewById<RelativeLayout>(R.id.recordlayout_container)
        val positionAdapt = itemView.findViewById<TextView>(R.id.recordlayout_position)
        val tagAdapt = itemView.findViewById<TextView>(R.id.recordlayout_tag)
        val addressAdapt = itemView.findViewById<TextView>(R.id.recordlayout_address)
        val moreAdapt = itemView.findViewById<ImageView>(R.id.recordlayout_more)
        val locationAdapt = itemView.findViewById<LinearLayout>(R.id.recordlayout_location_container)
        val longitudeAdapt = itemView.findViewById<TextInputEditText>(R.id.recordlayout_longitude)
        val latitudeAdapt = itemView.findViewById<TextInputEditText>(R.id.recordlayout_latitude)
        val buttonAdapt = itemView.findViewById<RelativeLayout>(R.id.recordlayout_button_container)
        val deleteAdapt = itemView.findViewById<TextView>(R.id.recordlayout_btnDelete)
        var openMoreAdapt : Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.location_row, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = arrayList.get(position)
        val stringPosition = "Location " + (position+1)
        item.id = position
        holder.positionAdapt.text = stringPosition
        holder.tagAdapt.text = item.tag
        holder.longitudeAdapt.setText(item.latlong.longitude.toString())
        holder.latitudeAdapt.setText(item.latlong.latitude.toString())
        item.marker.title = stringPosition
        holder.locationAdapt.visibility = View.GONE
        holder.buttonAdapt.visibility = View.GONE
        holder.addressAdapt.isSingleLine = true

        val geocoder = Geocoder(context)
        val geoAddress = geocoder.getFromLocation(item.latlong.latitude, item.latlong.longitude, 1)
        val geoResult = geoAddress[0].getAddressLine(0)
        holder.addressAdapt.text = geoResult.toString()

        //TODO : if marker onClick, perform moreAdapt.onClick (expanding more data)

        if(position%2==0) {
            holder.layoutAdapt.setBackgroundColor(ContextCompat.getColor(context,R.color.purple_50))
        }
        else{
            holder.layoutAdapt.setBackgroundColor(ContextCompat.getColor(context,R.color.white))
        }

        holder.moreAdapt.setOnClickListener {
            if(holder.openMoreAdapt){
                holder.locationAdapt.visibility = View.VISIBLE
                holder.buttonAdapt.visibility = View.VISIBLE
                holder.moreAdapt.setImageResource(R.drawable.ic_more_up)
                holder.openMoreAdapt = false
                holder.addressAdapt.isSingleLine = false
            }
            else{
                holder.locationAdapt.visibility = View.GONE
                holder.buttonAdapt.visibility = View.GONE
                holder.moreAdapt.setImageResource(R.drawable.ic_more_down)
                holder.openMoreAdapt = true
                holder.addressAdapt.isSingleLine = true
            }
        }

        holder.deleteAdapt.setOnClickListener{
            if(context is MarkLocation){
                context.deleteLocation(item)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun getLatLong(position : Int):LatLng{
        return arrayList[position].latlong
    }
}