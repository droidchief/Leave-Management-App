package com.victorloveday.leavemanager.ui.fragments

import HistoryAdapter
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.victorloveday.leavemanager.R
import com.victorloveday.leavemanager.api.RetrofitInstance
import com.victorloveday.leavemanager.database.model.Leave
import com.victorloveday.leavemanager.database.model.LeaveResponse
import com.victorloveday.leavemanager.databinding.FragmentHomeBinding
import com.victorloveday.leavemanager.ui.viewmodels.LeaveViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.util.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var leaveViewModel: LeaveViewModel
    private lateinit var response: Response<LeaveResponse>


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        historyAdapter = HistoryAdapter(requireContext())
        leaveViewModel = ViewModelProvider(requireActivity()).get(LeaveViewModel::class.java)

//        fetchUserLeaveHistoryFromAPI()

        setupRecentHistoryRecyclerView()

        //set backgrounds for colors
        binding.cancelLeave.setBackgroundResource(R.drawable.cancel_btn_bg)
        binding.editLeave.setBackgroundResource(R.drawable.edit_btn_bg)
        disablePendingLeaveButtons()

    }

    private fun setupRecentHistoryRecyclerView() = binding.recentLeavesRecyclerView.apply {

        //for demo purpose
//        val leave = Leave(0, "Going to the beach", "Casual Leave", "I'll need to travel for an emergency trip due to my father's coronation in Ibadan", "28 July", "2 August", "Declined")
//        leaveViewModel.saveLeave(leave)

        adapter = historyAdapter
        layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        leaveViewModel.readAllLeaveHistory.observe(viewLifecycleOwner, {

            if (it.isNotEmpty()) {

                if (it.size > 1) {
                    val slideFromRight = AnimationUtils.loadAnimation(requireContext(), R.anim.slide_in_right)
                    binding.recentLeavesRecyclerView.startAnimation(slideFromRight)
                    Handler(Looper.getMainLooper()).postDelayed({
                        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(0, -200)
                    }, 200)
                }

                //display maximum of 5 history
                val recentFive: MutableList<Leave> = ArrayList()
                if (it.size > 5) {
                    for (i in 0..4) {
                        val status = it[i].status
                        if (status != "Pending") {
                            recentFive.add(it[i])
                        }
                    }
                    historyAdapter.setData(recentFive)
                }else {
                    historyAdapter.setData(it)
                }

            } else {
                Toast.makeText(requireContext(), "Empty", Toast.LENGTH_SHORT).show()
            }

        })



    }

    private fun fetchUserLeaveHistoryFromAPI() {
        //make api request
        lifecycleScope.launchWhenCreated {
            response = try {
                RetrofitInstance.api.getLeaves("userId")

            } catch (e: IOException) {
                Toast.makeText(requireContext(), "Check your internet and try again...", Toast.LENGTH_SHORT).show()

                return@launchWhenCreated
            } catch (e: HttpException) {
                Toast.makeText(requireContext(), "Check your internet and try again...", Toast.LENGTH_SHORT).show()

                return@launchWhenCreated
            } catch (e: SocketTimeoutException) {
                Toast.makeText(requireContext(), "Check your internet and try again...", Toast.LENGTH_SHORT).show()

                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {

                if (!response.body()!!.error) {
                    //upsert response to data base
                    saveHistoryToDatabase()

                } else {
                    Toast.makeText(requireContext(), response.body()!!.message, Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(requireContext(), "Response not successful", Toast.LENGTH_SHORT).show()

                return@launchWhenCreated
            }

        }
    }

    private fun saveHistoryToDatabase() {
        leaveViewModel.readAllLeaveHistory
    }

    private fun disablePendingLeaveButtons() {
        binding.editLeave.isEnabled = false
        binding.cancelLeave.isEnabled = false
    }
    private fun enablePendingLeaveButtons() {
        binding.editLeave.isEnabled = true
        binding.cancelLeave.isEnabled = true
    }
}