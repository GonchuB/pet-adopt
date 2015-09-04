class Publication < ActiveRecord::Base
  belongs_to :pet
  belongs_to :user

  # TODO: Add references from picture and report
  # has_many :pictures
  # has_many :reports

  def publish!
    update_attributes(published: true)
  end

  def unpublish!
    update_attributes(published: false)
  end
end
