import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ProfileViewComponent } from './profile-view/profile-view.component';
import { ProfileEditComponent } from './profile-edit/profile-edit.component';
import { UploadPostComponent } from './upload-post/upload-post.component';

const routes: Routes = [
  { path: '', component:ProfileViewComponent },
  { path: 'edit', component:ProfileEditComponent },
  {path:'upload',component:UploadPostComponent},
  {path:'upload/:id',component:UploadPostComponent}

];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ProfileRoutingModule { }
