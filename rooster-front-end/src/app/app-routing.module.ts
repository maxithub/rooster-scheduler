import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CreateJobComponent } from './job/create-job/create-job.component';
import { SearchJobComponent } from './job/search-job/search-job.component';

const routes: Routes = [
  { path: 'job', component: CreateJobComponent },
  { path: 'jobs', component: SearchJobComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
