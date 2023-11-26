import { NgModule } from "@angular/core";
import { SharedModule } from "../shared/shared.module";
import { SelectScreenRoutingModule } from "./select-screen-routing.module";
import { SelectScreenComponent } from "./select-screen.component";

@NgModule({
  declarations: [SelectScreenComponent],
  imports: [SelectScreenRoutingModule, SharedModule],
})
export class SelectScreenModule {}
