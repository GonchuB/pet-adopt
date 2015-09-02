class CreatePets < ActiveRecord::Migration
  def change
    create_table :pets do |t|
      t.string :type
      t.string :name
      t.text :description

      t.timestamps null: false
    end
    add_index :pets, :type
  end
end
