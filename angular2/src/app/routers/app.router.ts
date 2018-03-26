import { Routes, RouterModule } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';

// components
import { EmptyComponent } from '../_components/empty/empty.component';
import { SignupComponent } from '../_components/signup/signup.component';
import { LoginComponent } from '../_components/login/login.component';


import { ProductNewComponent } from '../_components/product/product-new/product-new.component';
import { ProductDetailsComponent } from '../_components/product/product-details/product-details.component';

import { ChartsPageComponent } from '../_components/charts/charts-page/charts-page.component';

import { IndexPageComponent, OrdersReplenishPageComponent, OrdersSalesComponent } from '../_components/_index';


export const router: Routes = [

  { path: '_c/signup', component: SignupComponent },
  { path: '_c/login', component: LoginComponent },
  { path: '_c/product/new', component: ProductNewComponent },
  { path: '_c/product/details', component: ProductDetailsComponent },
  { path: '_c/charts', component: ChartsPageComponent },
  { path: '_c/p/:pid', component: ProductDetailsComponent },

  // { path: '', component: IndexPageComponent },
  // { path: 'index', component: IndexPageComponent },
  { path: '', component: OrdersSalesComponent },
  { path: 'index', component: OrdersSalesComponent },

  { path: 'registry', component: SignupComponent },
  { path: 'login', component: EmptyComponent },

  { path: 'p/:pid', component: ProductDetailsComponent },
  { path: 'store/p/:pid', component: ProductDetailsComponent },
  { path: 'store/:storeIdOrName', component: EmptyComponent },

  { path: 'mans/orders', component: EmptyComponent },
  { path: 'mans/orders/replenish', component: OrdersReplenishPageComponent },
  { path: 'mans/orders/sales', component: OrdersSalesComponent },
  { path: 'mans/orders/:orderType', component: EmptyComponent },
  { path: 'mans/orders/:orderType/:orderId', component: EmptyComponent },
  { path: 'mans/products', component: EmptyComponent },
  { path: 'mans/products/new', component: ProductNewComponent },
  { path: 'mans/products/:productId', component: EmptyComponent },
  { path: 'mans/products/:productId/edit', component: ProductNewComponent },
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

];

export const routers: ModuleWithProviders = RouterModule.forRoot(router);
