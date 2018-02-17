import { Routes, RouterModule } from '@angular/router';
import { ModuleWithProviders } from '@angular/core';

// components
import { EmptyComponent } from '../_components/empty/empty.component';
import { SignupComponent } from '../_components/signup/signup.component';


export const router: Routes = [

  { path: '_c/signup', component: SignupComponent },

  { path: '', component: EmptyComponent },
  { path: 'index', component: EmptyComponent },
  { path: 'registry', component: SignupComponent }

]

export const routers: ModuleWithProviders = RouterModule.forRoot(router);