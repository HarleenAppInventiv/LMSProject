package com.selflearningcoursecreationapp.ui.custom_camera

import android.annotation.SuppressLint
import android.content.ContentValues
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import com.selflearningcoursecreationapp.R
import com.selflearningcoursecreationapp.base.BaseFragment
import com.selflearningcoursecreationapp.databinding.FragmentCustomCameraBinding
import com.selflearningcoursecreationapp.extensions.invisible
import com.selflearningcoursecreationapp.utils.FileUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class CustomCameraFragment() : BaseFragment<FragmentCustomCameraBinding>()
//    , OnAdapterItemCallback<String>,
//    SwitchCameraListener
{
    //    private lateinit var binding: FragmentCustomCameraBinding
    private var imageList: ArrayList<String> = ArrayList()

    //    private var adapter: AddMoreImagesAdapter? = null
    private var clickType = -1
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var flashMode = ImageCapture.FLASH_MODE_AUTO
    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
    private var maxImage: Int? = 10


//    override fun initialiseFragmentBaseViewModel() {
//        binding.lifecycleOwner = this
//    }

//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View? {
//        // Inflate the layout for this fragment
//        binding = FragmentCustomCameraBinding.inflate(inflater, container, false)
//        return binding.root
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        (activity as CameraMainActivity).setSwitchListener(this)
        init()
        setAdapter()
        setListener()
        setWebView()
    }

    private fun setWebView() {
//        binding.myWebView.settings.loadsImagesAutomatically = true
//        binding.myWebView.settings.allowContentAccess = true
//        binding.myWebView.settings.domStorageEnabled = true
//        binding.myWebView.settings.javaScriptCanOpenWindowsAutomatically = true
//        binding.myWebView.settings.allowFileAccess = true
//        binding.myWebView.settings.mediaPlaybackRequiresUserGesture = false
//        binding.myWebView.settings.javaScriptEnabled = true
//        binding.myWebView.setBackgroundColor(Color.TRANSPARENT)
//        binding.myWebView.setLayerType(WebView.LAYER_TYPE_SOFTWARE, null)


//        val url = "https://avatusqaweb.appskeeper.in/test/transparent"
        val url = "https://threejs.avatus.com/examples/#webgl_morphtargets_face"
//        binding.myWebView.loadUrl(url)

//        binding.myWebView.webViewClient = object : WebViewClient() {
//            override fun onPageFinished(view: WebView, url: String?) {
//                super.onPageFinished(view, url)
//                view.setBackgroundColor(ContextCompat.getColor(context!!, R.color.transparent))
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                    view.setLayerType(View.LAYER_TYPE_HARDWARE, null)
//                } else {
//                    view.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
//                }
//            }
//        }

    }

    private fun init() {
//        maxImage = arguments?.getInt(IntentConstants.MAX_IMAGE,10)
        maxImage = 1
        startCamera(cameraSelector)
        cameraExecutor = Executors.newSingleThreadExecutor()
        showHideView(clickType)

        //hide other view
        binding.lCameraOption.ivAspectRatio.invisible()
        binding.lCameraOption.ivTimer.invisible()
    }


    @SuppressLint("RestrictedApi")
    private fun startCamera(cameraSelector: CameraSelector) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.surfaceView.surfaceProvider)


            }

            imageCapture = ImageCapture.Builder().setFlashMode(flashMode).build()

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)

            } catch (exc: Exception) {
                exc.printStackTrace()
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun setListener() {
//        binding.lCameraOption.ivCross.setOnClickListener {
//            requireActivity().onBackPressed()
//        }

        binding.btnCapture.setOnClickListener {
            // get the bitmap of the view using
            // getScreenShotFromView method it is
            // implemented below
            binding.btnCapture.isEnabled = false
            takePhoto()
        }

        binding.switchCamera.setOnClickListener {
            cameraSelector = if (cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA) {
                CameraSelector.DEFAULT_BACK_CAMERA
            } else {
                CameraSelector.DEFAULT_FRONT_CAMERA
            }
            startCamera(cameraSelector)
        }


        binding.lCameraOption.ivFlash.setOnClickListener {
            if (clickType == 1) {
                showHideView(-1)
            } else {
                showHideView(1)
            }
        }

        binding.lCameraOption.btnNext.setOnClickListener {
            sendResult()
        }
    }


    private fun showHideView(type: Int) {
        clickType = type
        binding.typeOfClick = type
        binding.executePendingBindings()
    }

    private fun setAdapter() {
//        binding.rvImages.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        adapter = AddMoreImagesAdapter(requireContext(), imageList,
//            object : OnAdapterItemCallback<String> {
//                override fun onClick(item: String, position: Int, type: Int) {
//                    super.onClick(item, position, type)
//                    showConfirmationDialog(position)
//                }
//            })
//        binding.rvImages.adapter = adapter
//        val ith = ItemTouchHelper(itemTouchListener)
//        ith.attachToRecyclerView(binding.rvImages)


        //Adapter for flash light
//        binding.lFlashLayout.tvRatio.text = getString(R.string.flash)
//        binding.lFlashLayout.rvRatio.layoutManager =
//            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
//        binding.lFlashLayout.rvRatio.adapter =
//            CameraArAdapter(viewType = AppConstants.CAMERA_OPTIONS.FLASH) {
//                // click listener
//                when (it) {
//                    0 -> {
//                        flashMode = ImageCapture.FLASH_MODE_AUTO
//                        startCamera(cameraSelector)
//                    }
//                    1 -> {
//                        flashMode = ImageCapture.FLASH_MODE_ON
//                        startCamera(cameraSelector)
//                    }
//                    2 -> {
//                        flashMode = ImageCapture.FLASH_MODE_OFF
//                        startCamera(cameraSelector)
//                    }
//                }
//            }
    }


    private fun takePhoto() {
        // Get a stable reference of the
        // modifiable image capture use case
        val imageCapture = imageCapture ?: return

        val timestamp = System.currentTimeMillis()

        val contentValues = ContentValues()
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp)
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")


        imageCapture.takePicture(
            ImageCapture.OutputFileOptions.Builder(
                requireActivity().contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build(),
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("+++", "" + exc.message.toString())
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    val uri = output.savedUri?.let { FileUtils.getPath(requireContext(), it) }
                    val savedUri = FileUtils.compressImage(
                        requireContext(),
                        uri,
                        cameraSelector == CameraSelector.DEFAULT_FRONT_CAMERA
                    )
                    // set the saved uri to the image view
                    if (savedUri != null) {

                        setFragmentResult(
                            "imageData",
                            bundleOf(
                                "fileUri" to savedUri.path
                            )
                        )
                        findNavController().navigateUp()
//                        if (imageList.isEmpty()) {
//                            showRetakeDialog(savedUri.toString())
//                        }
//                        else {
//                            imageList.add(savedUri.toString())
//                            adapter?.notifyDataSetChanged()
//
//                            if (imageList.size >= (maxImage ?: 10)) {
//                                binding.btnCapture.inVisible()
//                                AppUtils.showInfoBottomToast(
//                                    requireContext(),
//                                    getString(R.string.you_can_add_upto_10_images)
//                                )
//                            } else {
//                                binding.btnCapture.visible()
//                            }
//
//                            if (imageList.size >= 2) {
//                                if (SharedPreferencesManager.get().getBoolean(
//                                        AppConstants.PreferenceConstants.ALREADY_SAW_DRAG_HINT,
//                                        false
//                                    )
//                                ) {
//                                    if (binding.popHint.visibility != View.VISIBLE)
//                                        binding.popHint.gone()
//                                } else {
//                                    binding.popHint.visible()
//                                    SharedPreferencesManager.get().save(
//                                        AppConstants.PreferenceConstants.ALREADY_SAW_DRAG_HINT,
//                                        true
//                                    )
//                                }
//                            }
//                        }
                    }
                    binding.btnCapture.isEnabled = true
                }
            })
    }

//    private fun showRetakeDialog(image: String) {
//        val dialog = ChatCameraRetakeDialog(image, object : OnAdapterItemCallback<String> {
//            override fun onClick(item: String, position: Int, type: Int) {
//                super.onClick(item, position, type)
//                when (type) {
//                    0 -> {
//                        // send multiple image
//                        imageList.add(item)
//                        binding.lCameraOption.btnNext.visible()
//                        adapter?.notifyDataSetChanged()
//
//                    }
//                    1 -> { // send single image into chat
//                        imageList.add(item)
//                        sendResult()
//
//                    }
//                }
//            }
//        })
//        dialog.show(childFragmentManager, "")
//    }

    private fun sendResult() {
//        val intent = Intent()
//        intent.putStringArrayListExtra(IntentConstants.CHAT_MEDIA, imageList)
//        intent.putExtra(IntentConstants.MEDIA_TYPE, AppConstants.MESSAGE_TYPE.IMAGE)
//        requireActivity().setResult(Activity.RESULT_OK, intent)
//        requireActivity().finish()
    }


//    private fun showConfirmationDialog(position: Int) {
//        DialogUtils.confirmationDialog(context = requireContext(),
//            tittle = getString(R.string.remove_image),
//            message = getString(R.string.remove_image_message),
//            btnYes = getString(R.string.remove),
//            popUpDialogCallback = object : PopUpDialogCallback {
//                override fun onClickYes() {
//                    imageList.removeAt(position)
//                    adapter?.notifyDataSetChanged()
//                    binding.btnCapture.visible()
//                    if (imageList.isNullOrEmpty()) {
//                        binding.lCameraOption.btnNext.gone()
//                    }
//                }
//
//                override fun onClickNo() {}
//            })
//    }

/*
    private val itemTouchListener = object : ItemTouchHelper.Callback() {
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            return makeFlag(
                ItemTouchHelper.ACTION_STATE_DRAG,
                ItemTouchHelper.DOWN or ItemTouchHelper.UP or ItemTouchHelper.START or ItemTouchHelper.END
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            Collections.swap(
                imageList,
                viewHolder.bindingAdapterPosition,
                target.bindingAdapterPosition
            )
            adapter?.notifyItemMoved(
                viewHolder.bindingAdapterPosition,
                target.bindingAdapterPosition
            )
            return true
        }

        override fun onMoved(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            fromPos: Int,
            target: RecyclerView.ViewHolder,
            toPos: Int,
            x: Int,
            y: Int
        ) {
            super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y)
            adapter?.notifyItemChanged(viewHolder.bindingAdapterPosition)
            adapter?.notifyItemChanged(target.bindingAdapterPosition)
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.e("Recyclerview", "swiped")
        }
    }
*/


//    override fun onClick(item: String, position: Int, type: Int) {
//        super.onClick(item, position, type)
//        DialogUtils.confirmationDialog(context = requireContext(),
//            tittle = getString(R.string.remove_image),
//            message = getString(R.string.remove_image_message),
//            btnYes = getString(R.string.remove),
//            popUpDialogCallback = object : PopUpDialogCallback {
//                override fun onClickYes() {
//                    imageList.removeAt(position)
//                    adapter?.notifyItemChanged(position)
//
//                    if (imageList.size == 0) {
//                        binding.rvImages.gone()
//                    } else {
//                        if (imageList.size < 10) {
//                            binding.btnCapture.visible()
//                        } else
//                            binding.btnCapture.gone()
//
//                        binding.rvImages.visible()
//                    }
//
//                }
//
//                override fun onClickNo() {}
//            })
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()

    }

//    override fun onCameraSwitched() {
//        binding.switchCamera.performClick()
//    }

    override fun getLayoutRes(): Int {
        return R.layout.fragment_custom_camera
    }

    override fun onApiRetry(apiCode: String) {

    }

}