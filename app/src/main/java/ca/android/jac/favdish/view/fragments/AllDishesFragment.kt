package ca.android.jac.favdish.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ca.android.jac.favdish.R
import ca.android.jac.favdish.application.FavDishApplication
import ca.android.jac.favdish.databinding.DialogCustomListBinding
import ca.android.jac.favdish.databinding.FragmentAllDishesBinding
import ca.android.jac.favdish.model.entities.FavDish
import ca.android.jac.favdish.utils.Constants
import ca.android.jac.favdish.view.activities.AddUpdateDishActivity
import ca.android.jac.favdish.view.activities.MainActivity
import ca.android.jac.favdish.view.adapters.CustomListItemAdapter
import ca.android.jac.favdish.view.adapters.FavDishAdapter
import ca.android.jac.favdish.viewmodel.FavDishViewModel
import ca.android.jac.favdish.viewmodel.FavDishViewModelFactory
import ca.android.jac.favdish.viewmodel.HomeViewModel

class AllDishesFragment : Fragment() {

    private lateinit var mBinding: FragmentAllDishesBinding


    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    private lateinit var mFavDishAdapter: FavDishAdapter

    private lateinit var mCustomListDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding =
            FragmentAllDishesBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set the LayoutManager that this RecyclerView will use.
        mBinding.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        // Adapter class is initialized and list is passed in the param.
        mFavDishAdapter = FavDishAdapter(this@AllDishesFragment)
        // adapter instance is set to the recyclerview to inflate the items.
        mBinding.rvDishesList.adapter = mFavDishAdapter

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {

                if (it.isNotEmpty()) {

                    mBinding.rvDishesList.visibility = View.VISIBLE
                    mBinding.tvNoDishesAddedYet.visibility = View.GONE

                    mFavDishAdapter.dishesList(it)
                } else {

                    mBinding.rvDishesList.visibility = View.GONE
                    mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.showBottomNavigationView()
        }
    }

    fun dishDetails(favDish: FavDish) {

        if (requireActivity() is MainActivity) {
            (activity as MainActivity?)!!.hideBottomNavigationView()
        }

        findNavController()
            .navigate(AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_all_dishes, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_filter_dishes -> {
                filterDishesListDialog()
                return true
            }

            R.id.action_add_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateDishActivity::class.java))
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun deleteDish(dish: FavDish) {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.title_delete_dish))
        builder.setMessage(resources.getString(R.string.msg_delete_dish_dialog, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.lbl_yes)) { dialogInterface, _ ->
            mFavDishViewModel.delete(dish)
            dialogInterface.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)) { dialogInterface, which ->
            dialogInterface.dismiss()
        }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    private fun filterDishesListDialog() {
        mCustomListDialog = Dialog(requireActivity())

        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)

        mCustomListDialog.setContentView(binding.root)

        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)

        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(
            requireActivity(),
            this@AllDishesFragment,
            dishTypes,
            Constants.FILTER_SELECTION
        )
        binding.rvList.adapter = adapter
        mCustomListDialog.show()
    }

    fun filterSelection(filterItemSelection: String) {
        mCustomListDialog.dismiss()
        Log.i("Filter Selection", filterItemSelection)
        if (filterItemSelection == Constants.ALL_ITEMS) {
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner) { dishes ->
                dishes.let {
                    if (it.isNotEmpty()) {
                        mBinding.rvDishesList.visibility = View.VISIBLE
                        mBinding.tvNoDishesAddedYet.visibility = View.GONE
                        mFavDishAdapter.dishesList(it)
                    } else {
                        mBinding.rvDishesList.visibility = View.GONE
                        mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        } else {
            mFavDishViewModel.getFilteredList(filterItemSelection)
                .observe(viewLifecycleOwner) { dishes ->
                    dishes.let {
                        if (it.isNotEmpty()) {

                            mBinding.rvDishesList.visibility = View.VISIBLE
                            mBinding.tvNoDishesAddedYet.visibility = View.GONE

                            mFavDishAdapter.dishesList(it)
                        } else {

                            mBinding.rvDishesList.visibility = View.GONE
                            mBinding.tvNoDishesAddedYet.visibility = View.VISIBLE
                        }
                    }
                }
        }
    }
}