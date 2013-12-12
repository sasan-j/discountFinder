<?php
class Discounts_model extends CI_Model {

	public function __construct()
	{
		$this->load->database();
	}
	
	public function save_discount()
	{

	$data = array(
		'item_name' => $this->input->post('item_name'),
		'rate' => $this->input->post('rate'),
		'place_name' => $this->input->post('place_name'),
		'location_txt' => $this->input->post('location_txt'),
		'latitude' => $this->input->post('latitude'),
		'longitude' => $this->input->post('longitude'),
		'image_name' => $this->input->post('image_name'),
		'user_id' => $this->input->post('user_id'),
		'latitude' => $this->input->post('latitude')
	);

	return $this->db->insert('discounts', $data);
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
