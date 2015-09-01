module PetsHelper

	def pet_type_options
		options_for_select ["Dog", "Cat"]
	end

end
