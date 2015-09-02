Rails.application.routes.draw do
  devise_for :users

  resources :pets

  root "pets#index"
end
