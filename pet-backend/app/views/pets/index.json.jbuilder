json.array!(@pets) do |pet|
  json.extract! pet, :id, :type, :name, :description
  json.url pet_url(pet, format: :json)
end
