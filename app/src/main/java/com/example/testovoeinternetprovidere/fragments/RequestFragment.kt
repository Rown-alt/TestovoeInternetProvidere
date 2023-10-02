package com.example.testovoeinternetprovidere.fragments


import android.R
import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.WindowManager.LayoutParams
import android.view.inputmethod.InputMethod.SHOW_FORCED
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.testovoeinternetprovidere.adapters.HouseAdapter
import com.example.testovoeinternetprovidere.adapters.StreetAdapter
import com.example.testovoeinternetprovidere.databinding.FragmentRequestBinding
import com.example.testovoeinternetprovidere.viewModels.RequestViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class RequestFragment: Fragment() {
    private var _binding: FragmentRequestBinding? = null
    private val binding get() = _binding!!
    private val viewModel by viewModel<RequestViewModel>()
    lateinit var adapter: StreetAdapter
    lateinit var adapterHouse: HouseAdapter
    private var houseId = "-1"

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRequestBinding.inflate(layoutInflater)
        binding.streetEt.isFocusableInTouchMode = true
        var streetNames = mutableListOf<String>()
        var houseNumbers = mutableListOf<String>()
        binding.streetEt.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // Ничего не делаем
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length > 2){
                    // запрос по названию улицы
                    viewModel.getAllStreets(p0.toString())
                    binding.houseSpinner.visibility = View.GONE
                    viewModel.streets.observe(viewLifecycleOwner){
                        streetNames = mutableListOf()
                        if (it.isNotEmpty()){
                            for (i in it.indices){
                                streetNames.add(it[i].street)
                            }
                            adapter = StreetAdapter(requireContext(), streetNames)

                            binding.streetEt.setAdapter(adapter)
                            binding.streetEt.showDropDown()
                            Log.d("DropDownStreets", "Show")
                        }
                        else{
                            binding.streetEt.dismissDropDown()
                            Log.d("DropDownStreets", "Dismiss")
                        }

                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                val result = p0.toString() in streetNames

                if (result && p0.toString().isNotEmpty()){
                    // улица есть в бд, проводим наши манипуляции
                    viewModel.streets.removeObservers(viewLifecycleOwner)
                    binding.houseSpinner.visibility = View.VISIBLE
                    binding.streetEt.clearFocus()
                    requireContext().hideKeyboard(binding.root)

                    viewModel.houses.observe(viewLifecycleOwner){
                        houseNumbers = mutableListOf()
                        if (it.isNotEmpty()){
                            for (i in it.indices){
                                houseNumbers.add(it[i].house)
                            }
                        }
                        adapterHouse = HouseAdapter(requireContext(), houseNumbers)
                        binding.houseSpinner.adapter = adapterHouse
                    }
                }
                else{
                    // если такой улицы нет, то удаляем данные из ID улицы и показываем все окна
                    viewModel.streetId = "-1"
                    binding.house.visibility = View.VISIBLE
                    binding.building.visibility = View.VISIBLE
                }
            }
        })

        binding.streetEt.onItemClickListener =
            OnItemClickListener { p0, p1, p2, p3 ->
                // сохраняем ID улицы при нажатии
                viewModel.streetId = viewModel.streets.value?.get(p2)?.streetId ?: "-1"
                viewModel.getHouses(viewModel.streetId)
            }


        binding.houseSpinner.onItemSelectedListener = object : OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                // если выбран дом, прячем окна с домом и корпусом
                if (p2 != 0){
                    binding.house.visibility = View.GONE
                    binding.building.visibility = View.GONE
                    houseId = viewModel.houses.value!![p2].houseId
                }
                else{
                    houseId = "-1"
                    binding.house.visibility = View.VISIBLE
                    binding.building.visibility = View.VISIBLE
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // если не выбран дом, то снова показываем окна с домом и корпусом
                binding.house.visibility = View.VISIBLE
                binding.building.visibility = View.VISIBLE
            }
        }

        binding.sendBtn.setOnClickListener {
            // Не понял куда надо отправить запрос, но допустим, что тут post запрос с данными
            if (viewModel.streetId != "-1" && houseId != "-1"){
                showFirstType(viewModel.streetId, houseId, binding.flat.text.toString())
            }
            else if (viewModel.streetId != "-1" && houseId == "-1"){
                showSecondType(viewModel.streetId, binding.house.text.toString(), binding.building.text.toString(), binding.flat.text.toString())
            }
            else if (viewModel.streetId == "-1"){
                showThirdType(binding.streetEt.text.toString(), binding.house.text.toString(), binding.building.text.toString(), binding.flat.text.toString())
            }
        }

        binding.backPress.setOnClickListener {
            // Обрабатываем нажатие на иконку стрелки
            findNavController().popBackStack()
        }

        binding.root.setOnTouchListener { view, motionEvent ->
            // при нажатии на любую область прячем клавиатуру
            when(motionEvent.action){
                MotionEvent.ACTION_DOWN -> {
                    view.clearFocus()
                    requireContext().hideKeyboard(view)
                }
            }
            false
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // показываем клавиатуру и фокус на вводе
        binding.streetEt.requestFocus()
        requireActivity().window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Функция-расширение для убирания клавиатуры

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    // Ниже все возможные варианты вывода

    private fun showFirstType(streetId: String, houseId: String, flat: String){
        if (flat != ""){
            Toast.makeText(requireContext(), "ID улицы: $streetId, ID дома: $houseId, квартира: $flat", Toast.LENGTH_SHORT).show()
        }
        else{
            Toast.makeText(requireContext(), "Введите номер квартиры", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun showSecondType(streetId: String, houseNumber: String, building: String, flat: String){
        if (flat != "" && houseNumber!= ""){
            Toast.makeText(requireContext(), "ID улицы: $streetId, дом: $houseNumber, корпус: $building, квартира: $flat", Toast.LENGTH_SHORT).show()
        }
        else{
            if (houseNumber == ""){
                Toast.makeText(requireContext(), "Введите номер дома", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(), "Введите номер квартиры", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showThirdType(streetName: String, houseNumber: String, building: String, flat: String){
        if (flat != "" && houseNumber!= "" && streetName != ""){
            Toast.makeText(requireContext(), "улица: $streetName, дом: $houseNumber, корпус: $building, квартира: $flat", Toast.LENGTH_SHORT).show()
        }
        else{
            if (streetName == ""){
                Toast.makeText(requireContext(), "Введите улицу", Toast.LENGTH_SHORT).show()
            }
            else if (houseNumber == ""){
                Toast.makeText(requireContext(), "Введите номер дома", Toast.LENGTH_SHORT).show()
            }
            else{
                Toast.makeText(requireContext(), "Введите номер квартиры", Toast.LENGTH_SHORT).show()
            }
            
        }
    }
}