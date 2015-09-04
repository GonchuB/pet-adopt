json.array!(@publications) do |publication|
  json.extract! publication, :id, :needs_transit_home, :description, :pet_id, :user_id
  json.url publication_url(publication, format: :json)
end
