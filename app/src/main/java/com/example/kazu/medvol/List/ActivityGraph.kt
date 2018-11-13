package com.example.kazu.medvol.List

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.example.kazu.medvol.Models.Item_of_List
import com.example.kazu.medvol.R
import com.google.firebase.auth.FirebaseAuth
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.example.kazu.medvol.Models.Point
import com.google.firebase.database.*
import com.jjoe64.graphview.series.DataPointInterface
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.activity_graph.*
import android.widget.Toast
import com.jjoe64.graphview.series.Series
import com.jjoe64.graphview.series.OnDataPointTapListener



class ActivityGraph : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graph)

        val itemOfList = intent.getParcelableExtra<Item_of_List>(ActivityList.USER_KEY)

        val graphView = findViewById(R.id.graph) as GraphView
        val series = LineGraphSeries<DataPointInterface>()
        series.setOnDataPointTapListener { _, dataPoint -> Toast.makeText(this, "Series1: On Data Point clicked: $dataPoint", Toast.LENGTH_SHORT).show() }
        graphView.addSeries(series)

        listenForPlotData(itemOfList.item_key, series)


    }
    private fun listenForPlotData( itemid: String, series: LineGraphSeries<DataPointInterface>) {
        val userid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("logs/$userid/$itemid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val dp = arrayOfNulls<DataPoint>(p0.childrenCount.toInt())
                var point = Point()
                var point_past = Point()
                var index: Int = 0
                val children = p0.children

                children.forEach {
                    point = it.getValue(Point::class.java) ?:Point()
                    if (point.timestamp > point_past.timestamp) {
                        point_past = point
                        dp[index] = DataPoint(point.timestamp.toDouble(), point.rate.toDouble())
                        println("x: " + point.timestamp.toString() + " y: " + point.rate.toString() + " mass: " + point.mass.toString() + " time: " + point.date)
                        index++
                    }
                }
                graph_1_value.text = point.rate.toString()
                graph_2_value.text = point.mass.toString()

                series.resetData(dp)
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }
}