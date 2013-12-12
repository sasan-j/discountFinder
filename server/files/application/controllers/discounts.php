<?php
class Discounts extends CI_Controller {

	public function __construct()
	{
		parent::__construct();
		$this->load->model('discounts_model');
	}
/*
	public function index()
	{
	$data['discounts'] = $this->discounts_model->get_discounts();
	$data['title'] = 'News archive';

	$this->load->view('templates/header', $data);
	$this->load->view('news/index', $data);
	$this->load->view('templates/footer');
}

	public function view($slug)
	{
		$data['discounts'] = $this->discounts_model->get_discounts($slug);
	}

*/
	public function post_discount()
	{
		var_dump($_POST);

		var_dump($this->input->post());
		
		$config['upload_path'] = './uploads/';
		//$config['allowed_types'] = 'gif|jpg|png|txt';
		$config['max_size']	= '100';
		$config['max_width']  = '1024';
		$config['max_height']  = '768';
		$config['allowed_types'] = '*';

		$this->load->library('upload', $config);
		if (! $this->upload->do_upload('userfile')) {
        		$data['error'] = array('error' => $this->upload->display_errors());
    		}

    		else {
        		$data = array('upload_data' => $this->upload->data());	
        	}	
		// define variables and set to empty values
		$item_name = $rate = $location_txt = $latitude = $longitude = $user_id = "";
		
		if ($_SERVER["REQUEST_METHOD"] == "POST")
		{
			$result = $this->discounts_model->save_discount();
		}
		else echo "No post";

//INSERT INTO discounts (item_name, rate, location_txt, latitude, longitude, user_id ) VALUES ('_name', '4.5', 'location_txt', 'fdsgdfg', 'vfdgfds', '3534634');
//mysqli_query($db,"INSERT INTO discounts (item_name, rate, location_txt, latitude, longitude, user_id ) VALUES ('$item_name', '$rate', '$location_txt', //'$latitude', '$longitude', '$user_id')");


		//var_dump($_FILES);
		//var_dump($_POST);
		
		
	}
}