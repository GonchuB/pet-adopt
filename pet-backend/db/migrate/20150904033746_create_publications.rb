class CreatePublications < ActiveRecord::Migration
  def change
    create_table :publications do |t|
      t.boolean :needs_transit_home, default: false
      t.boolean :published, default: false
      t.text :description, default: ""
      t.references :pet, index: true
      t.references :user, index: true

      t.timestamps
    end
  end
end
