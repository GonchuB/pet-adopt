class PublicationCreateService < BaseService

  def initialize(user)
    @user = user
  end

  def call(pet_attributes, publication_attributes, publish: false)
    pet = pet_from_attributes(pet_attributes)

    publication = Publication.create(publication_attributes.merge(pet: pet, user: @user))
    publication.publish! if publish

    ModelResponse.new(publication)
  end

  private

  def pet_from_attributes(pet_attributes)
    if id = pet_attributes.delete(:id)
      Pet.find(id)
    else
      Pet.create(pet_attributes)
    end
  end

end
