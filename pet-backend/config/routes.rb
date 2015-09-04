Rails.application.routes.draw do
  resources :publications

  devise_for :users

  resources :pets

  root "pets#index"
end
