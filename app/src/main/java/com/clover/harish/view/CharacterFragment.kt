package com.clover.harish.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.clover.harish.R
import com.clover.harish.adapter.CharacterAdapter
import com.clover.harish.adapter.CharacterPagedAdapter
import com.clover.harish.adapter.ItemClickListener
import com.clover.harish.app.CloverApplication
import com.clover.harish.databinding.CharacterBinding
import com.clover.harish.models.CharacterVO
import com.clover.harish.models.viewmodels.AppViewModelFactory
import com.clover.harish.models.viewmodels.CharacterViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CharacterFragment : BaseFragment(), ItemClickListener<CharacterVO> {
    private lateinit var viewModel: CharacterViewModel
    private lateinit var binding: CharacterBinding
    private lateinit var adapter: CharacterPagedAdapter//CharacterAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val viewModelFactory = AppViewModelFactory(activity?.application as CloverApplication)
        viewModel = ViewModelProvider(this, viewModelFactory).get(CharacterViewModel::class.java)
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_character, container, false)

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    fun init() {
        adapter = CharacterPagedAdapter(this)
        //CharacterAdapter(this)


        binding.characterList.adapter = adapter
        viewModel.charactersLiveData.observe(viewLifecycleOwner, {
            Log.d("","--Result--")
//            adapter.setResult(it.results)
        })
//
//        viewModel.fetchCharacters()

        lifecycleScope.launch {
            viewModel.characters.collectLatest { pagedData ->
                adapter.submitData(pagedData)
            }
        }

        // added seearch view
        //
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                //Observable stream with rx
                viewModel.getcharacterByName(query)
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.d("","--")
                return true
            }

        })


//        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener ()
//        {
//            @Override
//            public boolean onQueryTextSubmit(String s) {
//                subject.onComplete();
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String text) {
//                subject.onNext(text);
//                return true;
//            }
//        });

//        return subject;
//    }
    }


    override fun onItemClicked(characterVO: CharacterVO) {
        val bundle = Bundle()
        bundle.putString("locationUrl", characterVO.location.url)
        bundle.putString("avatarUrl", characterVO.image)
        findNavController().navigate(
            R.id.action_characterFragment_to_characterDetailsFragment,
            bundle
        )

    }
}