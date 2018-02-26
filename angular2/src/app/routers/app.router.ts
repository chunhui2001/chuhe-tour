import { Routes, RouterModule } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';

// components
import { EmptyComponent } from '../_components/empty/empty.component';
import { SignupComponent } from '../_components/signup/signup.component';
import { LoginComponent } from '../_components/login/login.component';


import { ProductNewComponent } from '../_components/product/product-new/product-new.component';


export const router: Routes = [

  { path: '_c/signup', component: SignupComponent },
  { path: '_c/login', component: LoginComponent },
  { path: '_c/product/new', component: ProductNewComponent },

  { path: '', component: EmptyComponent },
  { path: 'index', component: EmptyComponent },
  { path: 'registry', component: SignupComponent },
  { path: 'login', component: EmptyComponent },
  { path: 'mans/orders', component: EmptyComponent },
  { path: 'mans/orders/:orderType', component: EmptyComponent },
  { path: 'mans/orders/:orderType/:orderId', component: EmptyComponent },
  { path: 'mans/products', component: EmptyComponent },
  { path: 'mans/products/new', component: ProductNewComponent },
  { path: 'mans/products/:productId', component: EmptyComponent },
  { path: 'mans/products/:productId/:oper', component: EmptyComponent },
  { path: 'mans/dealer', component: EmptyComponent },
  { path: 'mans/dealer/:dealerId', component: EmptyComponent },
  { path: 'mans/dealer/:dealerId/:oper', component: EmptyComponent },
  { path: 'mans/customer', component: EmptyComponent },
  { path: 'mans/customer/:customerId', component: EmptyComponent },
  { path: 'mans/customer/:customerId/:oper', component: EmptyComponent },
  { path: 'mans/user', component: EmptyComponent },
  { path: 'mans/user/:userId', component: EmptyComponent },
  { path: 'mans/user/:userId/:oper', component: EmptyComponent },
  { path: 'mans/priv', component: EmptyComponent },
  { path: 'mans/stock', component: EmptyComponent }

]

export const routers: ModuleWithProviders = RouterModule.forRoot(router);
