<?php
class Discounts_model extends CI_Model {

	public function __construct()
	{
		$this->load->database();
	}
	
	public function save_discount_data()
	{
        $upload_data = $this->upload->data();
        if($upload_data){
        	$file_name = $upload_data['file_name'];
        }

	$data = array(
		'item_category' => $this->input->post('item_category'),
		'item_name' => $this->input->post('item_name'),
		'item_rating' => $this->input->post('item_rating'),
		'item_place_name' => $this->input->post('item_place_name'),
		'item_location' => $this->input->post('item_location'),
		'item_location_latitude' => $this->input->post('item_location_latitude'),
		'item_location_longitude' => $this->input->post('item_location_longitude'),
		'item_pic' => $file_name,
		'user_id' => $this->input->post('user_id'),
		'item_comment' => $this->input->post('item_comment'),
		//'latitude' => $this->input->post('latitude')
	);

	return $this->db->insert('discounts', $data);
	}
	
	
	////////////////////////////////////////////
	public function load_discount_data()
	{
	//$limit=0;
	//$offset=0;
	$item_category = $this->input->post('item_category');
	$item_id = $this->input->post('item_id');
	if($item_category)
	  $query = $this->db->get_where('discounts', array('item_category' => $item_category));
	else if($item_id)
	  $query = $this->db->get_where('discounts', array('item_id' => $item_id));
	else return false;
	//$result = $query->row_array(); //For single row
	//$result = $query->result_array(); //For more than one row

	return $query->result();
	}
	
}
/*
public function get_discounts($slug = FALSE)
{
	if ($slug === FALSE)
	{
		$query = $this->db->get('discounts');
		return $query->result_array();
	}

	//$query = $this->db->get_where('discounts', array('slug' => $slug));
	//return $query->row_array();
}
*/